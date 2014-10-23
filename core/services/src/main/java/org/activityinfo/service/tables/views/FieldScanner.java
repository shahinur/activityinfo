package org.activityinfo.service.tables.views;

import com.google.common.base.Optional;
import org.activityinfo.model.expr.eval.FieldReader;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.service.store.ResourceCursor;

public class FieldScanner implements ColumnScanner {

    private FieldReader reader;
    private ColumnViewBuilder builder;
    private Optional<ColumnView> result;

    public FieldScanner(FieldReader reader, ColumnViewBuilder builder) {
        this.reader = reader;
        this.builder = builder;
    }

    @Override
    public void accept(ResourceCursor cursor) {
        builder.accept(reader.readField(cursor.getRecord()));
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
