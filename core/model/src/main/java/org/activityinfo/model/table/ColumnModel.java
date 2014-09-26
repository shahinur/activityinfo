package org.activityinfo.model.table;

import org.activityinfo.model.resource.IsRecord;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.RecordBuilder;
import org.activityinfo.model.resource.Records;

/**
 * Defines a Column within a Table request
 */
public class ColumnModel implements IsRecord {

    private String id;
    private ColumnType type;
    private ColumnSource source;
    private String expression;

    /**
     *
     * @return a unique, machine-readable stable id for this column
     * that is used to ensure stable references to other fields or
     * elements in the analysis.
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ColumnType getType() {
        return type;
    }

    public void setType(ColumnType type) {
        this.type = type;
    }

    public ColumnSource getSource() {
        return source;
    }

    public void setSource(ColumnSource source) {
        this.source = source;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    /**
     * Sets the source of this column to a new
     * {@code FieldSource}
     *
     * @return the newly created {@code FieldSource}
     */
    public FieldSource select() {
        FieldSource fieldSource = new FieldSource();
        this.source = fieldSource;
        return fieldSource;
    }

    public FieldSource select(ColumnType type) {
        FieldSource fieldSource = new FieldSource();
        this.source = fieldSource;
        this.type = type;
        return fieldSource;
    }

    public void selectId() {
        this.type = ColumnType.STRING;
        this.source = new ResourceIdSource();
    }

    @Override
    public Record asRecord() {
        RecordBuilder record = Records.builder();
        record.set("id", id);
        if(type != null) {
            record.set("type", type.name());
        }
        record.set("source", source.asRecord());
        return record.build();
    }

    public static ColumnModel fromRecords(Record record) {
        ColumnModel model = new ColumnModel();
        model.setId(record.getString("id"));

        if(record.has("type")) {
            model.setType(ColumnType.valueOf(record.getString("type")));
        }

        Record sourceRecord = record.getRecord("source");
        String sourceType = sourceRecord.getString("type");
        switch(sourceType) {
            case FieldSource.SOURCE_TYPE:
                model.setSource(FieldSource.fromRecord(sourceRecord));
                break;
            case ResourceIdSource.SOURCE_TYPE:
                model.setSource(new ResourceIdSource());
                break;
            default:
                throw new IllegalArgumentException(sourceType);
        }
        return model;
    }
}
