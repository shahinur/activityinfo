package org.activityinfo.model.annotation;

import org.activityinfo.model.type.FieldType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
public @interface Field {
    Class<? extends FieldType> type();
}
