package org.activityinfo.service.tables.views;

import org.activityinfo.model.type.FieldValue;

import java.util.Date;

public interface DateReader {

    Date readDate(FieldValue value);
}
