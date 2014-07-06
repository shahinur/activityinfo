package org.activityinfo.model.form.annotation;

import org.activityinfo.model.type.FieldType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
public @interface Field {
    String label() default "";
    Class<? extends FieldType> type();
}
