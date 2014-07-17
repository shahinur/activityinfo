package org.activityinfo.core.shared.type.converter;

import javax.annotation.Nonnull;

/**
 * Performs no conversion
 */
public enum NullConverter implements Converter  {
    INSTANCE;

    @Nonnull
    @Override
    public Object convert(@Nonnull Object value) {
        return value;
    }
}
