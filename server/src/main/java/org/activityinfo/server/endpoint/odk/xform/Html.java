package org.activityinfo.server.endpoint.odk.xform;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "http://www.w3.org/1999/xhtml")
public class Html {
    @XmlElement(namespace = "http://www.w3.org/1999/xhtml")
    public Head head;

    @XmlElement(namespace = "http://www.w3.org/1999/xhtml")
    public Body body;
}
