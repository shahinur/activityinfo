package org.activityinfo.service.tables.views;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.views.StringArrayColumnView;
import org.activityinfo.service.store.ResourceCursor;

import java.util.List;

public class IdColumnBuilder implements ColumnScanner {

    private List<String> ids = Lists.newArrayList();
    private Optional<ColumnView> result = Optional.absent();

    @Override
    public void accept(ResourceCursor cursor) {
        ids.add(cursor.getResourceId().asString());
    }

    @Override
    public void finalizeView() {
        result = Optional.<ColumnView>of(new StringArrayColumnView(ids));
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
