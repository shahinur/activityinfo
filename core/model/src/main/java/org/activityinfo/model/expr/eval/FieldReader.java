package org.activityinfo.model.expr.eval;

import org.activityinfo.model.record.Record;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldValue;

public interface FieldReader {

    FieldValue readField(Record record);

    FieldType getType();

}
