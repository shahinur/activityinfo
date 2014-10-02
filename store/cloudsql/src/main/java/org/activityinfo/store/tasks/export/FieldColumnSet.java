package org.activityinfo.store.tasks.export;

import com.google.common.base.Preconditions;
import org.activityinfo.model.expr.eval.FieldReader;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.type.FieldValue;

import java.util.List;

/**
 * Binding between a field and one or more columns.
 */
public class FieldColumnSet {
    private FormField field;
    private FieldReader reader;
    private List<Column> columns;

    public FieldColumnSet(FormField field, FieldReader reader, List<Column> columns) {
        Preconditions.checkNotNull(columns, field.getLabel() + ": columns");
        Preconditions.checkArgument(!columns.isEmpty(), field.getLabel() + ": columns list must be non-empty");
        this.field = field;
        this.reader = reader;
        this.columns = columns;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public String getHeading() {
        return field.getLabel();
    }

    public FieldValue read(Record record) {
        return reader.readField(record);
    }
}
