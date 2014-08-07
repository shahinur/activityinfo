package org.activityinfo.server.endpoint.odk.xform;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class Data {
    @XmlAttribute
    public String id;

    @XmlElement
    public Meta meta;

    @XmlAnyElement
    public List<JAXBElement<String>> jaxbElement;
}
