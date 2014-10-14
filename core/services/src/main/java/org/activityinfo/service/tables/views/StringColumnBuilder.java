package org.activityinfo.service.tables.views;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.views.ConstantColumnView;
import org.activityinfo.model.table.views.EmptyColumnView;
import org.activityinfo.model.table.views.StringArrayColumnView;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.service.tables.stats.StringStatistics;

import java.util.List;

public class StringColumnBuilder implements ColumnViewBuilder {

    private List<String> values = Lists.newArrayList();

    // Keep track of some statistics to
    private StringStatistics stats = new StringStatistics();

    private Optional<ColumnView> result = Optional.absent();

    private StringReader reader;

    public StringColumnBuilder(StringReader reader) {
        this.reader = reader;
    }

    @Override
    public void accept(FieldValue fieldValue) {
        String string = null;
        if(fieldValue != null) {
            string = reader.readString(fieldValue);
        }
        stats.update(string);
        values.add(string);
    }

    @Override
    public void finalizeView() {
        if(result.isPresent()) {
            throw new IllegalStateException();
        }
        result = Optional.of(build());
    }

    private ColumnView build() {
        if(stats.isEmpty()) {
            return new EmptyColumnView(values.size(), ColumnType.STRING);

        } else if(stats.isConstant()) {
            return new ConstantColumnView(values.size(), values.get(0));

        } else {
            return new StringArrayColumnView(values);
        }
    }

    @Override
    public ColumnView get() {
        return result.get();
    }
}
