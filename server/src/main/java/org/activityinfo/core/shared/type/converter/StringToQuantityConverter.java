package org.activityinfo.core.shared.type.converter;

import org.activityinfo.core.shared.type.formatter.QuantityFormatter;
import org.activityinfo.model.type.number.Quantity;

import javax.annotation.Nonnull;

/**
 * Converts string values to a quantity
 */
public class StringToQuantityConverter implements StringConverter<Quantity> {

    private final QuantityFormatter formatter;

    public StringToQuantityConverter(QuantityFormatter formatter) {
        this.formatter = formatter;
    }

    @Nonnull
    @Override
    public Quantity convert(@Nonnull String value) {
        return formatter.parse(value);
    }
}
