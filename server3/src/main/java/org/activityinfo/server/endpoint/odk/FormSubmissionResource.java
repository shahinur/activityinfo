package org.activityinfo.server.endpoint.odk;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.images.Image;
import com.google.common.io.ByteSource;
import com.google.inject.Inject;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.image.ImageRowValue;
import org.activityinfo.model.type.image.ImageValue;
import org.activityinfo.model.type.primitive.TextValue;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.blob.BlobMetadata;
import org.activityinfo.service.blob.UserBlobService;
import org.activityinfo.service.store.ResourceStore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.appengine.api.images.ImagesServiceFactory.makeImage;
import static com.google.common.io.ByteSource.wrap;
import static com.google.common.io.ByteStreams.toByteArray;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static javax.ws.rs.core.MediaType.TEXT_XML;
import static javax.ws.rs.core.Response.Status.*;
import static javax.ws.rs.core.Response.status;
import static org.activityinfo.server.endpoint.odk.OdkHelper.extractText;
import static org.activityinfo.server.endpoint.odk.OdkHelper.toRelativeFieldName;

@Path("/submission")
public class FormSubmissionResource {
    private static final Logger LOGGER = Logger.getLogger(FormSubmissionResource.class.getName());

    final private OdkFieldValueParserFactory factory;
    final private ResourceStore locator;
    final private AuthenticationTokenService authenticationTokenService;
    final private UserBlobService userBlobService;
    final private OdkFormSubmissionBackupService odkFormSubmissionBackupService;

    @Inject
    public FormSubmissionResource(OdkFieldValueParserFactory factory, ResourceStore locator,
                                  AuthenticationTokenService authenticationTokenService,
                                  UserBlobService userBlobService,
                                  OdkFormSubmissionBackupService odkFormSubmissionBackupService) {
        this.factory = factory;
        this.locator = locator;
        this.authenticationTokenService = authenticationTokenService;
        this.userBlobService = userBlobService;
        this.odkFormSubmissionBackupService = odkFormSubmissionBackupService;
    }

    @POST @Consumes(MULTIPART_FORM_DATA) @Produces(TEXT_XML)
    public Response submit(byte bytes[]) {
        ResourceId resourceId = Resources.generateId();

        try {
            odkFormSubmissionBackupService.backup(resourceId, ByteSource.wrap(bytes));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Form submission could not be backed up to GCS", e);
        }

        ByteArrayDataSource byteArrayDataSource = new ByteArrayDataSource(bytes, MULTIPART_FORM_DATA);
        MimeMultipart mimeMultipart;
        InputStream inputStream;
        DocumentBuilder documentBuilder;
        Document document;

        try {
            mimeMultipart = new MimeMultipart(byteArrayDataSource);
            inputStream = mimeMultipart.getBodyPart(0).getInputStream();
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = documentBuilder.parse(inputStream);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to parse input", e);
            return status(BAD_REQUEST).build();
        }

        document.normalizeDocument();

        NodeList nodeList = document.getElementsByTagName("instanceID");
        Node node = nodeList.getLength() == 1 ? nodeList.item(0) : null;

        if (isValid(node)) {
            String instanceId = extractText(node).replace("-", "");
            Node dataNode = node.getParentNode().getParentNode();

            if (dataNode.hasAttributes() && dataNode.getAttributes().getLength() == 1) {
                String tokenString = extractText(dataNode.getAttributes().item(0));

                if (tokenString != null && tokenString.length() > 0) {
                    return processForm(resourceId, mimeMultipart, document, instanceId, tokenString);
                }
            }
        }

        return status(BAD_REQUEST).build();
    }

    private Response processForm(ResourceId resourceId, MimeMultipart mimeMultipart, Document document,
                                 String instanceId, String tokenString) {
        int userId;
        int formClassId;
        AuthenticationToken authenticationToken = new AuthenticationToken(tokenString);
        AuthenticatedUser user;

        try {
            userId = authenticationTokenService.getUserId(authenticationToken);
            formClassId = authenticationTokenService.getFormClassId(authenticationToken);
            user = new AuthenticatedUser(userId);
        } catch (EntityNotFoundException entityNotFoundException) {
            return status(UNAUTHORIZED).build();
        } catch (RuntimeException runtimeException) {
            throw runtimeException;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Resource resource = locator.get(user, CuidAdapter.activityFormClass(formClassId)).getResource();
        FormClass formClass = FormClass.fromResource(resource);
        FormInstance formInstance = new FormInstance(ResourceId.valueOf(instanceId), formClass.getId());

        for (FormField formField : formClass.getFields()) {
            OdkFieldValueParser odkFieldValueParser = factory.fromFieldType(formField.getType());
            Node element = document.getElementsByTagName(toRelativeFieldName(formField.getId().asString())).item(0);

            if (element instanceof Element) {
                try {
                    formInstance.set(formField.getId(), odkFieldValueParser.parse((Element) element));
                } catch (Exception e) {
                    String text = extractText(element);

                    if (text == null) {
                        LOGGER.log(Level.SEVERE, "Malformed Element in form instance prevents parsing", e);
                    } else if (!text.equals("")) {
                        LOGGER.log(Level.WARNING, "Can't parse form instance contents, storing as text", e);
                        formInstance.set(formField.getId(), TextValue.valueOf(text));
                    }
                }
            }
        }

        for (Entry<ResourceId, FieldValue> entry : formInstance.getFieldValueMap().entrySet()) {
            FieldValue fieldValue = entry.getValue();

            if (fieldValue instanceof ImageValue) {
                ImageRowValue imageRowValue = ((ImageValue) fieldValue).getValues().get(0);

                if (imageRowValue.getFilename() == null) continue;

                try {
                    uploadImage(mimeMultipart, user, imageRowValue);
                } catch (FileNotFoundException fileNotFoundException) {
                    LOGGER.log(Level.SEVERE, "Could not find the specified filename", fileNotFoundException);
                    return status(BAD_REQUEST).build();
                } catch (MessagingException messagingException) {
                    LOGGER.log(Level.SEVERE, "Unable to parse input", messagingException);
                    return status(BAD_REQUEST).build();
                } catch (IOException ioException) {
                    LOGGER.log(Level.SEVERE, "Could not write image to GCS", ioException);
                    return status(SERVICE_UNAVAILABLE).build();
                }

                formInstance.set(entry.getKey(), fieldValue);
            }
        }

        formInstance.set(ResourceId.valueOf("backupBlobId"), resourceId.asString());

        locator.create(user, formInstance.asResource());
        return status(CREATED).build();
    }

    private void uploadImage(MimeMultipart mimeMultipart, AuthenticatedUser user, ImageRowValue imageRowValue)
            throws MessagingException, IOException {
        ByteSource byteSource = null;
        String mimeType = null, contentDisposition = null;  // Initialized just for the compiler

        for (int i = 0; i < mimeMultipart.getCount(); i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);

            if (imageRowValue.getFilename().equals(bodyPart.getFileName())) {
                Image image = makeImage(toByteArray(bodyPart.getInputStream()));

                contentDisposition = bodyPart.getDisposition();
                mimeType = bodyPart.getContentType();
                byteSource = wrap(image.getImageData());
                imageRowValue.setMimeType(mimeType);
                imageRowValue.setHeight(image.getHeight());
                imageRowValue.setWidth(image.getWidth());

                break;
            }
        }

        if (byteSource == null) throw new FileNotFoundException();

        BlobId blobId = BlobId.valueOf(imageRowValue.getBlobId());
        BlobMetadata metadata = BlobMetadata.attachment(blobId, imageRowValue.getFilename(), MediaType.valueOf(mimeType));

        userBlobService.put(user, metadata, byteSource);
    }

    /** A cascade of various validations of the structure of the submitted form, where node may have been set to null */
    private static boolean isValid(Node node) {
        return node != null &&
                "instanceID".equals(node.getNodeName()) &&
                "meta".equals(node.getParentNode().getNodeName()) &&
                "data".equals(node.getParentNode().getParentNode().getNodeName()) &&
                "#document".equals(node.getParentNode().getParentNode().getParentNode().getNodeName()) &&
                node.getParentNode().getParentNode().getParentNode().getParentNode() == null;
    }
}
