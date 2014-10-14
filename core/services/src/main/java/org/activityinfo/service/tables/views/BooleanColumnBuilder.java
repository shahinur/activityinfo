package org.activityinfo.service.tables.views;

import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.primitive.BooleanFieldValue;

import java.util.BitSet;

public class BooleanColumnBuilder implements ColumnViewBuilder {

    private BitSet values = new BitSet();
    private BitSet missing = new BitSet();
    private int index = 0;

    public BooleanColumnBuilder() {
    }

    @Override
    public void accept(FieldValue fieldValue) {
        if(fieldValue instanceof BooleanFieldValue) {
            values.set(index, fieldValue == BooleanFieldValue.TRUE);
        } else {
            missing.set(index, true);
        }
        index++;
    }

    @Override
    public void finalizeView() {

    }

    @Override
    public ColumnView get() {
        int numRows = index;
        if(missing.isEmpty()) {
            return new BitSetColumnView(numRows, values);
        } else {
            return new BitSetWithMissingView(numRows, values, missing);
        }
    }
}
