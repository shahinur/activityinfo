package org.activityinfo.service.tables.views;

import com.bedatadriven.rebar.time.calendar.LocalDate;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.type.component.ComponentReader;

import java.util.Date;
import java.util.List;

public class DateColumnBuilder implements ColumnViewBuilder {

    private ComponentReader<LocalDate> reader;

    private List<Date> values = Lists.newArrayList();

    private Optional<ColumnView> result = Optional.absent();

    public DateColumnBuilder(ComponentReader<LocalDate> dateReader) {
        this.reader = dateReader;
    }

    @Override
    public void putResource(Resource resource) {
        LocalDate localDate = reader.read(resource);
        if(localDate == null) {
            values.add(null);
        } else {
            values.add(localDate.atMidnightInMyTimezone());
        }
    }

    @Override
    public void finalizeView() {
        if(result.isPresent()) {
            throw new IllegalStateException();
        }
        result = Optional.of(build());
    }

    private ColumnView build() {
        return new DateArrayColumnView(values);
    }

    @Override
    public ColumnView get() {
        return result.get();
    }
}
