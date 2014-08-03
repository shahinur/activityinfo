package org.activityinfo.core.shared.type.converter;

import org.activityinfo.model.type.FieldValue;

import javax.annotation.Nonnull;

/**
 * Performs no conversion
 */
public enum NullConverter implements Converter  {
    INSTANCE;

    @Nonnull
    @Override
    public FieldValue convert(@Nonnull Object value) {
        return (FieldValue)value;
    }
}
