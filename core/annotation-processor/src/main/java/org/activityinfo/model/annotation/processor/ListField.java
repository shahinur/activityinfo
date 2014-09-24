package org.activityinfo.model.annotation.processor;

import javax.lang.model.element.ExecutableElement;

public class ListField {
    String name;
    ExecutableElement getter;
    String elementType;
    String elementFormType;

    public String getName() {
        return name;
    }

    public ExecutableElement getGetter() {
        return getter;
    }

    public String getElementType() {
        return elementType;
    }

    public String getElementFormType() {
        return elementFormType;
    }

    public String getGetterName() {
        return getter.getSimpleName().toString();
    }

    public String getTypeExpression() {
        return "new ListFieldType(new SubFormType(" + elementFormType + ".CLASS_ID" + "))";
    }
}
