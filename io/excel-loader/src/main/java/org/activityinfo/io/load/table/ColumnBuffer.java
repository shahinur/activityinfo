package org.activityinfo.io.load.table;

import com.google.common.collect.Lists;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.BooleanType;
import org.activityinfo.model.type.primitive.TextType;

import java.util.List;

public class ColumnBuffer {
    private List<Object> buffer = Lists.newArrayList();

    public ColumnBuffer() {
    }

    public void pushBool(int rowIndex, boolean b) {
        ensureRows(rowIndex);
        buffer.add(b);
    }

    public void pushEmpty() {
        buffer.add(null);
    }

    private void ensureRows(int rowIndex) {
        assert rowIndex >= buffer.size();
        while(this.buffer.size()+1 < rowIndex) {
            pushEmpty();
        }
    }

    public void pushNumber(int rowIndex, double v) {
        ensureRows(rowIndex);
        buffer.add(v);
    }

    public void pushString(int rowIndex, String s) {
        ensureRows(rowIndex);
        buffer.add(s);
    }

    public Object getRow(int rowIndex) {
        if(rowIndex < buffer.size()) {
            return buffer.get(rowIndex);
        }
        return null;
    }


    public FieldType guessType(int numHeaderRows) {

        int boolCount = 0;
        int numberCount = 0;
        int stringCount = 0;

        for(int i=numHeaderRows;i<buffer.size();++i) {
            Object value = buffer.get(i);
            if(value instanceof Boolean) {
                boolCount++;
            } else if(value instanceof Number) {
                numberCount++;
            } else if(value instanceof String && ((String) value).length() > 0) {
                stringCount++;
            }
        }

        double nonEmpty = (boolCount + numberCount + stringCount);
        double pctNumber = ((double)numberCount) / nonEmpty;
        double pctString = ((double)stringCount) / nonEmpty;
        double pctBool = ((double)boolCount) / nonEmpty;

//        System.out.println(String.format("[%s] number: %d (%.0f%%) boolean %d (%.0f%%) string %d (%.0f%%)",
//            buffer,
//            numberCount, pctNumber*100d,
//            boolCount, pctBool*100d,
//            stringCount, pctString*100d));

        if(pctNumber > 0.90 && (stringCount < 10)) {
            return new QuantityType();

        } else if(pctBool > 0.90 && (stringCount < 10)) {
            return BooleanType.INSTANCE;

        } else {
            return TextType.INSTANCE;
        }
    }

    public String getHeader() {
        if(buffer.size() > 0 && buffer.get(0) instanceof String) {
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
