package org.activityinfo.model.annotation;

/**
 * Specifies that a matching Serialization-Deserialization (Serde) class
 * should be generated to translate between a standard Java bean and a {@code Record}
 *
 */
public @interface RecordBean {
    String classId();
}
