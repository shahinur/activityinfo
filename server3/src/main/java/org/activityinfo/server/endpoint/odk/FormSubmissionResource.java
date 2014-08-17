package org.activityinfo.server.endpoint.odk;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import com.google.inject.Inject;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.FormDataParam;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.image.ImageRowValue;
import org.activityinfo.model.type.image.ImageValue;
import org.activityinfo.service.store.ResourceStore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.logging.Logger;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;

@Path("/submission")
public class FormSubmissionResource {
    private static final Logger LOGGER = Logger.getLogger(FormSubmissionResource.class.getName());

    final private OdkFieldValueParserFactory factory;
    final private ResourceStore locator;
    private AuthenticationTokenService authenticationTokenService;

    @Inject
    public FormSubmissionResource(OdkFieldValueParserFactory factory, ResourceStore locator,
                                  AuthenticationTokenService authenticationTokenService) {
        this.factory = factory;
        this.locator = locator;
        this.authenticationTokenService = authenticationTokenService;
    }

    @POST @Consumes(MediaType.MULTIPART_FORM_DATA) @Produces(MediaType.TEXT_XML)
    public Response submit(@FormDataParam("xml_submission_file") String xml, FormDataMultiPart formDataMultiPart) {
        //TODO Not everything is fully tested yet and especially authorization is still missing
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(xml.getBytes(Charsets.UTF_8));
        DocumentBuilder documentBuilder;
        Document document;

        try {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = documentBuilder.parse(byteArrayInputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        document.normalizeDocument();

        NodeList nodeList = document.getElementsByTagName("instanceID");
        Node node = nodeList.getLength() == 1 ? nodeList.item(0) : null;

        // A cascade of various validations of the structure of the submitted form, where node may have been set to null
        if (node != null &&
                "instanceID".equals(node.getNodeName()) &&
                "meta".equals(node.getParentNode().getNodeName()) &&
                "data".equals(node.getParentNode().getParentNode().getNodeName()) &&
                "#document".equals(node.getParentNode().getParentNode().getParentNode().getNodeName()) &&
                node.getParentNode().getParentNode().getParentNode().getParentNode() == null) {
            Node dataNode = node.getParentNode().getParentNode();

            if (dataNode.hasAttributes() && dataNode.getAttributes().getLength() == 1) {
                String tokenString = OdkHelper.extractText(dataNode.getAttributes().item(0));

                if (tokenString != null && tokenString.length() > 0) {
                    int userId;
                    int formClassId;
                    AuthenticationToken authenticationToken = new AuthenticationToken(tokenString);
                    AuthenticatedUser user;
                    try {
                        userId = authenticationTokenService.getUserId(authenticationToken);
                        formClassId = authenticationTokenService.getFormClassId(authenticationToken);
                        user = new AuthenticatedUser("XYZ", userId, "@");
                    } catch (EntityNotFoundException entityNotFoundException) {
                        throw new WebApplicationException(Response.Status.UNAUTHORIZED);
                    } catch (RuntimeException runtimeException) {
                        throw runtimeException;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    Resource resource = locator.get(user, CuidAdapter.activityFormClass(formClassId));
                    FormClass formClass = FormClass.fromResource(resource);
                    FormInstance formInstance = new FormInstance(ResourceId.generateId(), formClass.getId());

                    for (FormField formField : formClass.getFields()) {
                        OdkFieldValueParser odkFieldValueParser = factory.fromFieldType(formField.getType());
                        Node element = document.getElementsByTagName(
                                OdkHelper.toRelativeFieldName(formField.getId().asString())).item(0);

                        if (element instanceof Element) {
                            formInstance.set(formField.getId(), odkFieldValueParser.parse((Element) element));
                        }
                    }

                    for (FieldValue fieldValue : formInstance.getFieldValueMap().values()) {
                        if (fieldValue instanceof ImageValue) {
                            ImageRowValue imageRowValue = ((ImageValue) fieldValue).getValues().get(0);
                            FormDataBodyPart formDataBodyPart = formDataMultiPart.getField(imageRowValue.getFilename());
                            imageRowValue.setMimeType(formDataBodyPart.getMediaType().toString());
                            ByteSource byteSource = ByteSource.wrap(formDataBodyPart.getValueAs(byte[].class));
                            /* TODO Find a way to configure the BlobFieldStorageService correctly
                            try {
                                gcsBlobFieldStorageService.put(user, new BlobId(imageRowValue.getBlobId()), byteSource);
                            } catch (IOException ioException) {
                                LOGGER.log(Level.SEVERE, "Could not write image to GCS", ioException);
                                return Response.status(SERVICE_UNAVAILABLE).build();
                            }
                            End of non-functional code segment */
                        }
                    }

                    locator.put(user, formInstance.asResource());
                    return Response.status(CREATED).build();
                }
            }
        }

        return Response.status(BAD_REQUEST).build();
    }
}
