package org.activityinfo.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Specifies that the annotated property should not
 * be serialized as a record field
 */
@Target(ElementType.METHOD)
public @interface Transient {
}
