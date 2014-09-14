package org.activityinfo.model.annotation.processor;

import javax.lang.model.element.ExecutableElement;

public class FieldModel {

    private String name;
    private TypeModel typeModel;
    private ExecutableElement getter;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TypeModel getTypeModel() {
        return typeModel;
    }

    public void setTypeModel(TypeModel typeModel) {
        this.typeModel = typeModel;
    }

    public ExecutableElement getGetter() {
        return getter;
    }

    public void setGetter(ExecutableElement getter) {
        this.getter = getter;
    }
}
