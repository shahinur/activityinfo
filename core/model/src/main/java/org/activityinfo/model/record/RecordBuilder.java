package org.activityinfo.model.record;

import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldValue;

public interface RecordBuilder {

    /**
     * Sets the {@code classId} of this Record. Short cut for
     * setClassId(ResourceId.valueOf(classId))
     * @param classId
     */
    RecordBuilder setClassId(ResourceId classId);


    /**
     * Sets the {@code classId} of this Record. Short cut for
     * setClassId(ResourceId.valueOf(classId))
     * @param classId
     */
    RecordBuilder setClassId(String classId);


    /**
     * Sets the value of the given field to {@code value} if
     * {@code value} is not {@code null}.
     *
     * If {@code value} is {@code null}, any existing field value
     * is removed.
     *
     * @param fieldName the name of the field whose value is to be set.
     * @param value the new field value.
     */
    RecordBuilder set(String fieldName, String value);

    /**
     * Sets the value of the given field to {@code value}
     *
     * @param fieldName the name of the field whose value is to be set.
     * @param value the new field value.
     */
    RecordBuilder set(String fieldName, double value);

    /**
     * Sets the value of the given field to {@code value}
     *
     * @param fieldName the name of the field whose value is to be set.
     * @param value the new field value.
     */
    RecordBuilder set(String fieldName, boolean value);

    /**
     * Sets the value of the given field to {@code value} if
     * {@code value} is not {@code null}.
     *
     * If {@code value} is {@code null}, any existing field value
     * is removed.
     *
     * @param fieldName the name of the field whose value is to be set.
     * @param value the new field value.
     */
    RecordBuilder set(String fieldName, Record value);

    /**
     * Sets the value of the given field to {@code value} if
     * {@code value} is not {@code null}.
     *
     * If {@code value} is {@code null}, any existing field value
     * is removed.
     *
     * @param fieldName the name of the field whose value is to be set.
     * @param value the new field value.
     */
    RecordBuilder set(String fieldName, IsRecord value);

    /**
     * Sets the value of the given field to {@code value} if
     * {@code value} is not {@code null}.
     *
     * If {@code value} is {@code null}, any existing field value
     * is removed.
     *
     * @param fieldName the name of the field whose value is to be set.
     * @param value the new field value.
     */
    RecordBuilder setFieldValue(String fieldName, FieldValue value);

    RecordBuilder set(String fieldName, Iterable<?> value);

    RecordBuilder set(String fieldName, ResourceId value);

    RecordBuilder set(String fieldName, Enum<?> enumValue);

    RecordBuilder setAll(Record record);

    Record build();

}
