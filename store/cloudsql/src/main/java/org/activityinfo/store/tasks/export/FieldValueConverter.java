package org.activityinfo.store.tasks.export;

import org.activityinfo.model.type.FieldValue;

import javax.annotation.Nonnull;

public interface FieldValueConverter<T extends FieldValue> {

    Object convertValue(@Nonnull T fieldValue);

}
