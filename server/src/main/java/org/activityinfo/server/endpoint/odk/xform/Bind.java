package org.activityinfo.server.endpoint.odk.xform;

import javax.xml.bind.annotation.XmlAttribute;

public class Bind {
    @XmlAttribute
    public String nodeset;

    @XmlAttribute
    public String type;

    @XmlAttribute
    public String readonly;

    @XmlAttribute
    public String calculate;

    @XmlAttribute
    public String required;

    @XmlAttribute
    public String constraint;

    @XmlAttribute(namespace = "http://openrosa.org/javarosa")
    public String constraintMsg;
}
