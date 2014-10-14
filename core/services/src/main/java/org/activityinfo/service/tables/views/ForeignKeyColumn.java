package org.activityinfo.service.tables.views;

import com.google.common.collect.Multimap;
import org.activityinfo.model.resource.ResourceId;

import java.util.Collection;

public class ForeignKeyColumn {

    /**
     * Maps row index to ResourceId of related entity
     */
    private Multimap<Integer, ResourceId> keys;
    private int numRows;

    public ForeignKeyColumn(int numRows, Multimap<Integer, ResourceId> keys) {
        this.numRows = numRows;
        this.keys = keys;
    }

    public int getNumRows() {
        return numRows;
    }

    public Collection<ResourceId> getKeys(int rowIndex) {
        return keys.get(rowIndex);
    }
}
