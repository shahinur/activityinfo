package org.activityinfo.server.endpoint.odk.xform;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class PresentationElement {
    @XmlAttribute
    public String ref;

    @XmlAttribute
    public String mediatype;

    @XmlElement
    public String label;

    @XmlElement
    public List<Item> item;

    @XmlElement
    public String hint;
}
