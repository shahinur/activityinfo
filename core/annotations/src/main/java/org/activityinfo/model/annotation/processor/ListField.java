package org.activityinfo.model.annotation.processor;

import javax.lang.model.element.ExecutableElement;

public class ListField {
    String fieldName;
    ExecutableElement getter;
    String elementType;

    public String getName() {
        return fieldName;
    }

    public ExecutableElement getGetter() {
        return getter;
    }

    public String getElementType() {
        return elementType;
    }

    public String getElementClassType() {
        return elementType + "Class";
    }

    public String getGetterName() {
        return getter.getSimpleName().toString();
    }

    public String getTypeExpression() {
        return "new ListFieldType(new RecordFieldType(" + getElementClassType() + ".CLASS_ID" + "))";
    }
}
