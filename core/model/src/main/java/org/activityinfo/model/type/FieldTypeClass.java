package org.activityinfo.model.type;

import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.type.geo.GeoPointType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.time.LocalDateType;

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

    public static final String TYPE_FIELD_NAME = "type";


    /**
     *
     * @return a string uniquely identifying this {@code FieldTypeClass}. This
     * identifier will be stored with all values of types in this class.
     */
    String getId();


    /**
     * @return a human readable label describing this type class
     */
    String getLabel();

    /**
     * Creates an instance of this {@code FieldTypeClass} using the parameters
     * specified by given the record.
     */
    FieldType createType(Record typeParameters);

    /**
     *
     * @return an instance of this {@code FieldTypeClass} using default parameters
     */
    FieldType createType();

    /**
     *
     * @return a FormClass that describes the FieldType's parameters
     */
    FormClass getParameterFormClass();


    // intermediate step to support refactoring


    public static final QuantityType.TypeClass QUANTITY = QuantityType.TypeClass.INSTANCE;

    public static final NarrativeType NARRATIVE = NarrativeType.INSTANCE;

    public static final TextType FREE_TEXT = TextType.INSTANCE;

    public static final LocalDateType LOCAL_DATE = LocalDateType.INSTANCE;

    public static final GeoPointType GEOGRAPHIC_POINT = GeoPointType.INSTANCE;

    public static final BooleanType BOOLEAN = BooleanType.INSTANCE;

    public static final ReferenceType.TypeClass REFERENCE = ReferenceType.TypeClass.INSTANCE;

}
