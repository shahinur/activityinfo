package org.activityinfo.service.tasks.appengine.export;

import org.activityinfo.model.type.FieldValue;

import javax.annotation.Nonnull;

public interface FieldValueConverter<T extends FieldValue> {

    Object convertValue(@Nonnull T fieldValue);

}
