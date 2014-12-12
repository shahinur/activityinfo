package org.activityinfo.model.record;

import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.ResourceId;

/**
 * Serializes and deserializes a Java bean between
 * a Record a Java bean.
 *
 * @param <T>
 */
public interface RecordBeanClass<T> {

    ResourceId getClassId();

    Record toRecord(T bean);

    T toBean(Record record);

    FormClass get();

}
