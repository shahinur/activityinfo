package org.activityinfo.model.annotation.processor;

import com.google.common.collect.Lists;
import org.activityinfo.model.annotation.DefaultBooleanValue;
import org.activityinfo.model.annotation.RecordBean;

import java.util.List;

@RecordBean(classId = "_baz")
public class Baz {

    private String label;
    private boolean visible;

    private final List<Foo> children = Lists.newArrayList();

    public void setLabel(String label) {
        this.label = label;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getLabel() {
        return label;
    }

    @DefaultBooleanValue(true)
    public boolean isVisible() {
        return visible;
    }

    public List<Foo> getChildren() {
        return children;
    }
}
