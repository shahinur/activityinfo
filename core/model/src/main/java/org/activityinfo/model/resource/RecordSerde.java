package org.activityinfo.model.resource;

import org.activityinfo.model.form.FormClass;

/**
 * Serializes and deserializes a Java bean between
 * a Record a Java bean.
 *
 * @param <T>
 */
public interface RecordSerde<T> {

    Record toRecord(T bean);

    T toBean(Record record);

    FormClass getFormClass();

}
