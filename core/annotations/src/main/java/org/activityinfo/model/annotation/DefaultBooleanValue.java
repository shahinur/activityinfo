package org.activityinfo.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
public @interface DefaultBooleanValue {
    boolean value();
}
