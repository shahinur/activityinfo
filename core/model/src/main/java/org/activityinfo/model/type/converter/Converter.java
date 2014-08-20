package org.activityinfo.model.type.converter;

import org.activityinfo.model.type.FieldValue;

import javax.annotation.Nonnull;

/**
 * Converts raw imported values from the {@code ImportSource} to the
 * correct field value
 */
public interface Converter<K, V extends FieldValue> {

    /**
     * Converts the non-null {@code value} to the correct type
     */
    public V convert(@Nonnull K value);
}
