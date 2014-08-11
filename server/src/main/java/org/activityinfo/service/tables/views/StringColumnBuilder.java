package org.activityinfo.service.tables.views;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.table.columns.ConstantColumnView;
import org.activityinfo.model.table.columns.EmptyColumnView;
import org.activityinfo.model.table.columns.StringArrayColumnView;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.TypeRegistry;
import org.activityinfo.model.type.component.ComponentReader;
import org.activityinfo.model.type.primitive.HasStringValue;
import org.activityinfo.service.tables.reader.StringFieldReader;
import org.activityinfo.service.tables.stats.StringStatistics;

import java.util.List;

public class StringColumnBuilder implements ColumnViewBuilder {

    private final StringFieldReader reader;
    private List<String> values = Lists.newArrayList();

    // Keep track of some statistics to
    private StringStatistics stats = new StringStatistics();

    private Optional<ColumnView> result = Optional.absent();

    public StringColumnBuilder(StringFieldReader reader) {
        this.reader = reader;
    }

    @Override
    public void putResource(Resource resource) {
        String string = reader.readString(resource);
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
            return new EmptyColumnView(ColumnType.STRING, values.size());
        } else if(stats.isConstant()) {
            return new ConstantColumnView(values.get(0), values.size());
        } else {
            return new StringArrayColumnView(values);
        }
    }

    @Override
    public ColumnView get() {
        return result.get();
    }
}
