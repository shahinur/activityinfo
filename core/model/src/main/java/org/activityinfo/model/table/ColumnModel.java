package org.activityinfo.model.table;

import org.activityinfo.model.table.summary.UniqueValue;

/**
 * Defines a Column within a Table request
 */
public class ColumnModel {

    private String id;
    private ColumnType type = ColumnType.STRING;
    private ColumnSource source;

    private SummaryFunction summaryFunction = UniqueValue.INSTANCE;

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

    public SummaryFunction getSummaryFunction() {
        return summaryFunction;
    }

    public void setSummaryFunction(SummaryFunction summaryFunction) {
        this.summaryFunction = summaryFunction;
    }
}
