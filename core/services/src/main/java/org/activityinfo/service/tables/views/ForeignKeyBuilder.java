package org.activityinfo.service.tables.views;

import com.google.common.base.Supplier;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.activityinfo.model.form.FormEvalContext;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.ReferenceValue;


public class ForeignKeyBuilder implements FormSink, Supplier<ForeignKeyColumn> {

    private final String fieldName;
    private int rowIndex = 0;
    private Multimap<Integer, ResourceId> keys = HashMultimap.create();

    public ForeignKeyBuilder(String fieldName) {
        this.fieldName = fieldName;
    }

    public void accept(FormEvalContext resource) {
        ReferenceValue referenceValue = (ReferenceValue) resource.getFieldValue(fieldName);
        if(referenceValue != null) {
            for (ResourceId id : referenceValue.getResourceIds()) {
                keys.put(rowIndex, id);
            }
        }
        rowIndex++;
    }

    public ForeignKeyColumn get() {
        return new ForeignKeyColumn(keys);
    }
}
