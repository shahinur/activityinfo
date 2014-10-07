package org.activityinfo.service.tables.views;

import org.activityinfo.model.expr.eval.FieldReader;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.table.ColumnView;

public class FieldScanner implements ColumnScanner {

    private FieldReader reader;
    private ColumnViewBuilder builder;

    public FieldScanner(FieldReader reader, ColumnViewBuilder builder) {
        this.reader = reader;
        this.builder = builder;
    }

    @Override
    public void accept(ResourceId resourceId, Record record) {
        builder.accept(reader.readField(record));
    }

    @Override
    public ColumnView get() {
        return builder.get();
    }

    @Override
    public void finalizeView() {
        builder.finalizeView();
    }
}
