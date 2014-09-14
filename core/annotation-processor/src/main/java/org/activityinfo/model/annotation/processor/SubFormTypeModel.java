package org.activityinfo.model.annotation.processor;

import javax.lang.model.element.TypeElement;

public class SubFormTypeModel implements TypeModel {

    private TypeElement classElement;

    public SubFormTypeModel(TypeElement classElement) {
        this.classElement = classElement;
    }

}
