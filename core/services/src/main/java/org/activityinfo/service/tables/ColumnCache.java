package org.activityinfo.service.tables;

import com.google.common.base.Supplier;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.table.ColumnView;

import java.util.Map;
import java.util.Set;

public interface ColumnCache {

    Map<String, ColumnView> getIfPresent(ResourceId id, Set<String> strings);

    void put(ResourceId id, Map<String, ? extends Supplier<ColumnView>> columnMap);
}
