package org.activityinfo.service.core.tables.views;

import com.google.common.collect.Multimap;
import org.activityinfo.model.resource.ResourceId;

import java.util.Collection;

public class ForeignKeyColumn {

    /**
     * Maps row index to ResourceId of related entity
     */
    private Multimap<Integer, ResourceId> keys;

    public ForeignKeyColumn(Multimap<Integer, ResourceId> keys) {
        this.keys = keys;
    }

    public int getNumRows() {
        return keys.keys().size();
    }

    public Collection<ResourceId> getKeys(int rowIndex) {
        return keys.get(rowIndex);
    }
}
