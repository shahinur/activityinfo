package org.activityinfo.server.endpoint.odk;

import com.google.common.base.Charsets;
import com.google.inject.Inject;
import com.sun.jersey.multipart.FormDataParam;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.store.ResourceStore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;

@Path("/submission")
public class FormSubmissionResource {
    final private OdkFieldValueParserFactory factory;
    final private ResourceStore locator;

    @Inject
    public FormSubmissionResource(OdkFieldValueParserFactory factory, ResourceStore locator) {
        this.factory = factory;
        this.locator = locator;
    }

    @POST @Consumes(MediaType.MULTIPART_FORM_DATA) @Produces(MediaType.TEXT_XML)
    public Response submit(@FormDataParam("xml_submission_file") String xml) throws Exception {
        //TODO Not everything is fully tested yet and especially Cardinality.MULTIPLE will definitely not work correctly
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(xml.getBytes(Charsets.UTF_8));
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(byteArrayInputStream);
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
                String id = OdkHelper.extractText(dataNode.getAttributes().item(0));

                if (id != null && id.length() > 0) {
                    Resource resource = locator.get(ResourceId.create(id));
                    FormClass formClass = FormClass.fromResource(resource);
                    FormInstance formInstance = new FormInstance(ResourceId.generateId(), formClass.getId());

                    for (FormField formField : formClass.getFields()) {
                        OdkFieldValueParser odkFieldValueParser = factory.fromFieldType(formField.getType());
                        Node element = document.getElementsByTagName("field_" + formField.getId().asString()).item(0);

                        if (element instanceof Element) {
                            formInstance.set(formField.getId(), odkFieldValueParser.parse((Element) element));
                        }
                    }

                    //TODO Determine owner
                    locator.createResource(null, formInstance.asResource());
                    return Response.status(CREATED).build();
                }
            }
        }

        return Response.status(BAD_REQUEST).build();
    }
}
