package org.activityinfo.service.core.tables.views;

import com.google.common.base.Supplier;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.ReferenceValue;


public class ForeignKeyBuilder implements ResourceSink, Supplier<ForeignKeyColumn> {

    private final String fieldName;
    private int rowIndex = 0;
    private Multimap<Integer, ResourceId> keys = HashMultimap.create();

    public ForeignKeyBuilder(String fieldName) {
        this.fieldName = fieldName;
    }

    public void putResource(Resource resource) {
        ReferenceValue referenceValue = ReferenceValue.fromRecord(resource.getRecord(fieldName));
        for(ResourceId id : referenceValue.getResourceIds()) {
            keys.put(rowIndex, id);
        }
        rowIndex++;
    }

    public ForeignKeyColumn get() {
        return new ForeignKeyColumn(keys);
    }
}
