package org.activityinfo.core.shared.type.converter;

import org.activityinfo.core.shared.type.formatter.QuantityFormatter;

import javax.annotation.Nonnull;

/**
 * Converts string values to a quantity
 */
public class StringToQuantityConverter implements StringConverter<Double> {

    private final QuantityFormatter formatter;

    public StringToQuantityConverter(QuantityFormatter formatter) {
        this.formatter = formatter;
    }

    @Nonnull
    @Override
    public Double convert(@Nonnull String value) {
        return formatter.parse(value);
    }
}
