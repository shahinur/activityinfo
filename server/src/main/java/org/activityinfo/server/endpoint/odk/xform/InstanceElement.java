package org.activityinfo.server.endpoint.odk.xform;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;

@XmlJavaTypeAdapter(InstanceElementAdapter.class)
public class InstanceElement {
    private String id;
    private String name;
    private String value;
    private List<InstanceElement> children = Lists.newArrayList();

    public InstanceElement(String name) {
        this.name = name;
    }

    public InstanceElement(String name, InstanceElement... children) {
        this.name = name;
        this.children = Lists.newArrayList(children);
    }

    public InstanceElement(String name, String value) {
        this.name = name;
        this.value = value;
    }


    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = Strings.emptyToNull(value);
    }

    public List<InstanceElement> getChildren() {
        return children;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean hasChildren() {
        return this.children.size() > 0;
    }

    public void addChild(InstanceElement instanceElement) {
        children.add(instanceElement);
    }
}
