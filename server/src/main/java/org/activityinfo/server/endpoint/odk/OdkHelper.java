package org.activityinfo.server.endpoint.odk;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class OdkHelper {
    static String extractText(Node node) {
        NodeList childNodes = node.getChildNodes();

        if (childNodes.getLength() == 0) return "";

        if (childNodes.getLength() == 1) {
            Node child = childNodes.item(0);
            if (child.getChildNodes().getLength() == 0 && "#text".equals(child.getNodeName())) {
                return child.getNodeValue();
            }
        }

        return null;
    }
}
