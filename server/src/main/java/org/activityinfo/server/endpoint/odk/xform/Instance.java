package org.activityinfo.server.endpoint.odk.xform;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.namespace.QName;

public class Instance {

    private InstanceElement root;

    public Instance() {
    }

    public Instance(InstanceElement root) {
       this.root = root;
    }

    @XmlAnyElement
    public InstanceElement getRoot() {
        return root;
    }

    public void setRoot(InstanceElement root) {
        this.root = root;
    }
}
