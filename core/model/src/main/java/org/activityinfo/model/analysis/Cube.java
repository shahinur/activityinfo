package org.activityinfo.model.analysis;

import org.activityinfo.model.table.Bucket;

import java.util.Collection;

public class Cube {

    private PivotTableModel model;
    private final Collection<Bucket> buckets;

    public Cube(PivotTableModel model, Collection<Bucket> values) {
        this.model = model;
        this.buckets = values;
    }
}
