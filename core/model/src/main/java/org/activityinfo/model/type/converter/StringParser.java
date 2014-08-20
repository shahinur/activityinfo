package org.activityinfo.model.type.converter;

import org.activityinfo.model.type.primitive.TextValue;

import javax.annotation.Nonnull;

public enum StringParser implements StringConverter<TextValue> {

    INSTANCE;

    @Override
    public TextValue convert(@Nonnull String value) {
        return TextValue.valueOf(value);
    }
}
