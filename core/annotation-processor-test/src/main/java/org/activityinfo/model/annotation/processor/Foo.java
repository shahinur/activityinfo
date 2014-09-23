package org.activityinfo.model.annotation.processor;

import org.activityinfo.model.annotation.RecordBean;

@RecordBean(classId = "_foo")
public class Foo {

    private String name;

    public Foo() {
    }

    public Foo(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
