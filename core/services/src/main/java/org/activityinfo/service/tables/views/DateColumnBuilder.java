package org.activityinfo.service.tables.views;

import com.google.common.base.Optional;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.type.FieldValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateColumnBuilder implements ColumnViewBuilder {

    private List<Date> values = new ArrayList<>();

    private final DateReader reader;

    private Optional<ColumnView> result = Optional.absent();

    public DateColumnBuilder(DateReader reader) {
        this.reader = reader;
    }

    @Override
    public void accept(FieldValue fieldValue) {
        values.add(reader.readDate(fieldValue));
    }

    @Override
    public void finalizeView() {
        result = Optional.<ColumnView>of(new DateArrayColumnView(values));
    }

    @Override
    public ColumnView get() {
        return result.get();
    }
}
