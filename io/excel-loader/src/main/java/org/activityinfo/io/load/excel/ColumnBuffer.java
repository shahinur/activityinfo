package org.activityinfo.io.load.excel;

import com.google.common.collect.Lists;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.BooleanType;
import org.activityinfo.model.type.primitive.TextType;

import java.util.List;

public class ColumnBuffer {
    private List<Object> buffer = Lists.newArrayList();

    private int nonEmptyCount = 0;
    private int booleanCount = 0;
    private int numberCount = 0;
    private int stringCount = 0;

    private int columnIndex = 0;

    public ColumnBuffer(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public void push(boolean b) {
        buffer.add(b);
        nonEmptyCount++;
        booleanCount++;
    }

    public void pushEmpty() {
        buffer.add(null);
    }

    public void pushNumber(double v) {
        buffer.add(v);
        nonEmptyCount++;
        numberCount++;
    }

    public void pushString(String s) {
        buffer.add(s);
        nonEmptyCount++;
        stringCount++;
    }

    public void dump() {
        System.out.println(getHeader() + ": " + buffer.size() + ", numberCount = " + numberCount + "," +
            "stringCount = " + stringCount);
    }

    public Object getRow(int rowIndex) {
        if(rowIndex < buffer.size()) {
            return buffer.get(rowIndex);
        }
        return null;
    }

    public FieldType guessType() {
        if(stringCount < 10 && numberCount > 10) {
            return new QuantityType().setUnits("units");
        } else if(booleanCount > 10) {
            return BooleanType.INSTANCE;
        } else {
            return TextType.INSTANCE;
        }
    }

    public String getHeader() {
        if(buffer.get(0) instanceof String) {
            return (String) buffer.get(0);
        }
        return null;
    }

    public int getRowCount() {
        return buffer.size();
    }

    public String getString(int rowIndex) {
        Object value = getRow(rowIndex);
        if (value != null) {
            return value.toString();
        }
        return null;
    }
}
