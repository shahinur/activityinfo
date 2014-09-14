package org.activityinfo.model.annotation.processor;

import org.activityinfo.model.type.FieldType;

public class SingletonTypeModel implements TypeModel {

    private Class<? extends FieldType> fieldType;

    public SingletonTypeModel(Class<? extends FieldType> fieldType) {


        this.fieldType = fieldType;
    }
}
