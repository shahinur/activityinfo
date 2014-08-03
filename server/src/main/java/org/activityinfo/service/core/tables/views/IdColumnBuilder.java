package org.activityinfo.service.core.tables.views;

import com.google.common.collect.Lists;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.columns.StringArrayColumnView;

import java.util.List;

public class IdColumnBuilder implements ColumnViewBuilder {

    private List<String> ids = Lists.newArrayList();


    @Override
    public void putResource(Resource resource) {
        ids.add(resource.getId().asString());
    }

    @Override
    public void finalizeView() {

    }

    @Override
    public ColumnView get() {
        return new StringArrayColumnView(ids);
    }
}
