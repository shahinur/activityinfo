package org.activityinfo.model.annotation.processor;

import org.activityinfo.model.annotation.Field;
import org.activityinfo.model.annotation.RecordBean;

@RecordBean(classId = "_foo")
public class Foo {

    private String name;
    private String _import;

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

    @Field(name = "importAlias")
    public String getImport() {
        return _import;
    }

    public void setImport(String name) {
        this._import = name;
    }
}
