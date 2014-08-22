package org.activityinfo.client.xform;


import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import org.activityinfo.model.resource.ResourceId;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.UUID;

public class XFormInstanceBuilder {

    private Document doc;
    private final Element data;

    private List<BodyPart> files = Lists.newArrayList();

    public XFormInstanceBuilder(String accessToken)  {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Could not create XML document builder", e);
        }

        // root elements
        doc = docBuilder.newDocument();
        data = doc.createElement("data");
        data.setAttribute("id", accessToken);
        data.appendChild(createMeta());

        doc.appendChild(data);
    }

    private Element createMeta() {
        Element meta = doc.createElement("meta");
        meta.appendChild(createInstanceID());
        return meta;
    }

    private Element createInstanceID() {
        Element instanceId = doc.createElement("instanceID");
        instanceId.setTextContent(UUID.randomUUID().toString());
        return instanceId;
    }

    public void addFieldValue(ResourceId fieldId, String value) {
        Element element = doc.createElement("field_" + fieldId.asString());
        element.setTextContent(value);
        data.appendChild(element);
    }

    public void addImageFieldValue(ResourceId fieldId, String imageFileName, ByteSource image) throws IOException {
        FormDataBodyPart part = new FormDataBodyPart();
        part.setEntity(image.read());
        part.setMediaType(MediaType.valueOf("image/png"));
        part.setName(imageFileName);
        part.setContentDisposition(
                FormDataContentDisposition
                        .name(imageFileName)
                        .fileName(imageFileName)
                        .build());
        addFieldValue(fieldId, imageFileName);
        files.add(part);
    }

    public FormDataMultiPart build() {
        FormDataBodyPart xmlPart = new FormDataBodyPart(toXml(), MediaType.APPLICATION_XML_TYPE);
        FormDataMultiPart entity = new FormDataMultiPart();
        entity.bodyPart(xmlPart);

        for(BodyPart bodyPart : files) {
            entity.bodyPart(bodyPart);
        }
        return entity;
    }

    public String toXml()  {
        // write the content into xml file
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StringWriter stringWriter = new StringWriter();
            StreamResult result = new StreamResult(stringWriter);

            transformer.transform(source, result);

            return stringWriter.toString();
        } catch(TransformerException e) {
            throw new RuntimeException("Exception writing document as XML: " + e.getMessage(), e);
        }
    }
}