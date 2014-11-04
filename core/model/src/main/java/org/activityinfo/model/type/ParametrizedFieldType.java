package org.activityinfo.model.type;

import org.activityinfo.model.resource.Record;

/**
 * A {@code FieldType} with parameters that further specialize
 * the type class.
 */
public interface ParametrizedFieldType extends FieldType {

    /**
     *
     * @return a {@code Record} containing this type's parameters.
     */
    Record getParameters();

}
