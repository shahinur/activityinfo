package org.activityinfo.io.odk.xform;

import javax.xml.bind.annotation.XmlElement;

public class Head {
    @XmlElement(namespace = "http://www.w3.org/1999/xhtml")
    public String title;

    @XmlElement
    public Model model;
}
