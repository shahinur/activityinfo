package org.activityinfo.model.type;

/**
 * Defines a class of Field Types.
 *
 * FieldTypeClass are meant to provide a very specific types of fields
 * at a high level of abstraction. For example, beyond simply a "number" type,
 * we will also have a QuantityType, a RatioType, a CurrencyType, etc, that carry
 * logic with them about how they should be aggregated, indexed, etc.
 *
 * FieldTypeClasses can be further specialized with parameters: for example, the
 * QuantityType takes a "units" parameter.
 *
 */
public interface FieldTypeClass {

    /**
     *
     * @return a string uniquely identifying this {@code FieldTypeClass}. This
     * identifier will be stored with all values of types in this class.
     */
    String getId();


    /**
     *
     * @return an instance of this {@code FieldTypeClass} using default parameters
     */
    FieldType createType();


}
