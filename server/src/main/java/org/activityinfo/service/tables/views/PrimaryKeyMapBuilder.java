package org.activityinfo.service.tables.views;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;

import java.util.Map;

/**
 * Builds a mapping from {@code ResourceId} to row index
 */
public class PrimaryKeyMapBuilder implements ResourceSink, Supplier<PrimaryKeyMap> {

    private final Map<ResourceId, Integer> map = Maps.newHashMap();
    private int rowIndex = 0;

    @Override
    public void putResource(Resource resource) {
        map.put(resource.getId(), rowIndex++);
    }

    @Override
    public PrimaryKeyMap get() {
        return new PrimaryKeyMap(map);
    }
}
