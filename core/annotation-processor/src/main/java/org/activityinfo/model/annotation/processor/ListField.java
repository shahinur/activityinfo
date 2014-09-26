package org.activityinfo.model.annotation.processor;

import javax.lang.model.element.ExecutableElement;

public class ListField {
    String name;
    ExecutableElement getter;
    String elementType;

    public String getName() {
        return name;
    }

    public ExecutableElement getGetter() {
        return getter;
    }

    public String getElementType() {
        return elementType;
    }

    public String getElementSerdeType() {
        return elementType + "Serde";
    }

    public String getGetterName() {
        return getter.getSimpleName().toString();
    }

    public String getTypeExpression() {
        return "new ListFieldType(new RecordFieldType(" + getElementSerdeType() + ".CLASS_ID" + "))";
    }
}
