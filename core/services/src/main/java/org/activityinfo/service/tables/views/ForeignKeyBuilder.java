package org.activityinfo.service.tables.views;

import com.google.common.base.Supplier;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.activityinfo.model.expr.eval.FieldReader;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.ReferenceValue;


public class ForeignKeyBuilder implements InstanceSink, Supplier<ForeignKeyColumn> {

    private final FieldReader fieldReader;
    private int rowIndex = 0;
    private Multimap<Integer, ResourceId> keys = HashMultimap.create();

    public ForeignKeyBuilder(FieldReader reader) {
        this.fieldReader = reader;
    }

    @Override
    public void accept(ResourceId resourceId, Record record) {
        FieldValue fieldValue = fieldReader.readField(record);
        if(fieldValue instanceof ReferenceValue) {
            ReferenceValue referenceValue = (ReferenceValue) fieldValue;
            for (ResourceId id : referenceValue.getResourceIds()) {
                keys.put(rowIndex, id);
            }
        }
        rowIndex++;
    }

    public ForeignKeyColumn get() {
        return new ForeignKeyColumn(rowIndex, keys);
    }
}
