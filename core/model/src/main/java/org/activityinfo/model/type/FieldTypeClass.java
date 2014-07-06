package org.activityinfo.model.type;

import org.activityinfo.model.resource.Record;

/**
 * Defines a class of Field Types.
 *
 * FieldTypeClass are meant to provide a very
 *
 */
public interface FieldTypeClass {

    public static final String TYPE_FIELD_NAME = "type";

    /**
     *
     * @return a string uniquely identifying this {@code FieldTypeClass}. This
     * identifier will be stored with all values of types in this class.
     */
    String getId();

    /**
     * Creates an instance of this {@code FieldTypeClass} using the parameters
     * specified by given the record.
     */
    FieldType createType(Record typeParameters);

}
