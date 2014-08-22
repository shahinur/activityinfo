package org.activityinfo.client.xform;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public class XFormItem {

    private String url;
    private String label;

    @XmlAttribute
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @XmlValue
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "XForm{" +
               "label='" + label + '\'' +
               '}';
    }
}
