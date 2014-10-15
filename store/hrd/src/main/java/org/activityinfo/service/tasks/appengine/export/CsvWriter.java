package org.activityinfo.service.tasks.appengine.export;

import com.google.common.base.Charsets;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.type.FieldValue;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.List;

public class CsvWriter {

    private final List<FieldColumnSet> fields;
    private final String[] row;
    private final CSVPrinter writer;
    private final DecimalFormat numberFormat;

    public CsvWriter(List<FieldColumnSet> fields, OutputStream out) throws IOException {
        this.writer = new CSVPrinter(new OutputStreamWriter(out, Charsets.UTF_8), CSVFormat.DEFAULT);
        this.fields = fields;
        this.row = new String[countTotalColumns()];
        this.numberFormat = createNumberFormat();
        writeHeaders();
    }

    private DecimalFormat createNumberFormat() {
        // format numbers as machine readable
        DecimalFormat numberFormat = new DecimalFormat();
        numberFormat.setGroupingUsed(false);
        numberFormat.setMinimumFractionDigits(0);
        numberFormat.setMaximumFractionDigits(6);
        return numberFormat;
    }

    private int countTotalColumns() {
        int columnCount = 0;
        for(FieldColumnSet field : fields) {
            columnCount += field.getColumns().size();
        }
        return columnCount;
    }

    private void writeHeaders() throws IOException {
        int columnIndex = 0;
        for(FieldColumnSet field : fields) {
            if (field.getColumns().size() == 1) {
                row[columnIndex++] = field.getHeading();
            } else {
                for (Column column : field.getColumns()) {
                    row[columnIndex++] = field.getHeading() + " - " + column.getHeading();
                }
            }
        }
        writer.printRecord((Object[])row);
    }

    public void writeRow(Record record) throws IOException {
        int columnIndex = 0;
        for(FieldColumnSet field : fields) {
            FieldValue value = field.read(record);
            for(Column column : field.getColumns()) {
                if(value == null) {
                    row[columnIndex++] = null;
                } else {
                    row[columnIndex++] = toString(column.convert(value));
                }
            }
        }
        writer.printRecord((Object[])row);
    }

    private String toString(Object value) {
        if(value instanceof Number) {
            return numberFormat.format(value);
        } else if(value == null) {
            return null;
        } else {
            return value.toString();
        }
    }

    public void close() throws IOException {
        writer.close();
    }
}
