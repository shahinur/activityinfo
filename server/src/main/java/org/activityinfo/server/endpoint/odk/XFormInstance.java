package org.activityinfo.server.endpoint.odk;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.images.Image;
import com.google.common.base.Optional;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.sun.jersey.multipart.BodyPart;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.primitive.TextValue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.appengine.api.images.ImagesServiceFactory.makeImage;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

/**
 * Provides methods to access the contents of a submitted XFormInstance
 */
public class XFormInstance {

    private static final Logger LOGGER = Logger.getLogger(XFormInstance.class.getName());
    private Document document;
    private AuthenticationToken authenticationToken;
    private String instanceId;
    private MimeMultipart mimeMultipart;

    public XFormInstance(byte[] bytes) {
        ByteArrayDataSource byteArrayDataSource = new ByteArrayDataSource(bytes, MediaType.MULTIPART_FORM_DATA);

        parseDocument(byteArrayDataSource);
        parseHeader();
    }

    private void parseDocument(ByteArrayDataSource byteArrayDataSource) {
        try {
            mimeMultipart = new MimeMultipart(byteArrayDataSource);
            InputStream inputStream = mimeMultipart.getBodyPart(0).getInputStream();
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = documentBuilder.parse(inputStream);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to parse input", e);
            throw new WebApplicationException(Response.status(BAD_REQUEST).build());
        }

        document.normalizeDocument();
    }

    private void parseHeader() {
        NodeList nodeList = document.getElementsByTagName("instanceID");
        Node node = nodeList.getLength() == 1 ? nodeList.item(0) : null;

        // A cascade of various validations of the structure of the submitted form, where node may have been set to null
        String tokenString;
        if (node == null ||
                !"instanceID".equals(node.getNodeName()) ||
                !"meta".equals(node.getParentNode().getNodeName()) ||
                !"data".equals(node.getParentNode().getParentNode().getNodeName()) ||
                !"#document".equals(node.getParentNode().getParentNode().getParentNode().getNodeName()) ||
                node.getParentNode().getParentNode().getParentNode().getParentNode() != null) {
            throw new WebApplicationException(Response.status(BAD_REQUEST).build());
        }

        instanceId = OdkHelper.extractText(node).replace("-", "");
        Node dataNode = node.getParentNode().getParentNode();

        if (dataNode.hasAttributes() && dataNode.getAttributes().getLength() == 1) {
            tokenString = OdkHelper.extractText(dataNode.getAttributes().item(0));

            if (tokenString != null && tokenString.length() > 0) {
                authenticationToken = new AuthenticationToken(tokenString);
            }
        }
    }

    public AuthenticationToken getAuthenticationToken() {
        return authenticationToken;
    }

    public String getId() {
        return instanceId;
    }

    public Optional<Element> getFieldContent(ResourceId id) {
        String tagName = OdkHelper.toRelativeFieldName(id.asString());
        NodeList nodeList = document.getElementsByTagName(tagName);

        if (nodeList.getLength() > 0) {
            return Optional.of((Element) nodeList.item(0));
        } else {
            return Optional.absent();
        }
    }

    public MimeMultipart getMimeMultipart() {
        return mimeMultipart;
    }

    public javax.mail.BodyPart findBodyPartByFilename(String filename) throws MessagingException {
        for (int i = 0; i < mimeMultipart.getCount(); i++) {
            javax.mail.BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (filename.equals(bodyPart.getFileName())) {
                return bodyPart;
            }
        }
        LOGGER.log(Level.SEVERE, "Could not find the specified filename");
        throw new WebApplicationException(Response.status(BAD_REQUEST).build());
    }
}
