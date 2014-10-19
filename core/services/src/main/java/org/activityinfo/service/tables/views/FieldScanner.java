package org.activityinfo.service.tables.views;

import com.google.common.base.Optional;
import org.activityinfo.model.expr.eval.FieldReader;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.table.ColumnView;

public class FieldScanner implements ColumnScanner {

    private FieldReader reader;
    private ColumnViewBuilder builder;
    private Optional<ColumnView> result;

    public FieldScanner(FieldReader reader, ColumnViewBuilder builder) {
        this.reader = reader;
        this.builder = builder;
    }

    @Override
    public void accept(ResourceId resourceId, Record record) {
        builder.accept(reader.readField(record));
    }

    @Override
    public void finalizeView() {
        builder.finalizeView();
        result = Optional.of(builder.get());
    }

    @Override
    public void useCached(ColumnView view) {
        result = Optional.of(view);
    }

    @Override
    public ColumnView get() {
        return result.get();
    }

}
