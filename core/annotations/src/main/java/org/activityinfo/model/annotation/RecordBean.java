package org.activityinfo.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Specifies that a matching Serialization-Deserialization (Serde) class
 * should be generated to translate between a standard Java bean and a {@code Record}
 *
 */
@Target(ElementType.TYPE)
public @interface RecordBean {
    String classId();
}
