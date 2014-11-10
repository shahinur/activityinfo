package org.activityinfo.server.endpoint.odk.xform;

import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlType(propOrder = {"instance", "bindings"})
public class Model {

    private Instance instance = new Instance();
    private List<Bind> bindings = new ArrayList<>();

    @XmlElement
    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    @XmlElement(name = "bind")
    public List<Bind> getBindings() {
        return bindings;
    }

    public void addBinding(Bind bind) {
        this.bindings.add(bind);
    }
}
