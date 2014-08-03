package org.activityinfo.model.table;

import com.google.common.collect.Lists;
import org.activityinfo.model.resource.IsRecord;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.Records;
import org.activityinfo.model.resource.ResourceId;

import java.util.List;

/**
 * Describes a Table to be constructed from a
 * FormTree.
 */
public class TableModel implements IsRecord {

    private final List<RowSource> rowSources = Lists.newArrayList();
    private final List<ColumnModel> columns = Lists.newArrayList();

    public TableModel() {
    }

    /**
     * Creates a new TableModel using the given {@code classId} as the
     * root FormClassId
     */
    public TableModel(ResourceId classId) {
        rowSources.add(new RowSource(classId));
    }

    public List<RowSource> getRowSources() {
        return rowSources;
    }

    public List<ColumnModel> getColumns() {
        return columns;
    }

    public ColumnModel addColumn(String id) {
        ColumnModel column = new ColumnModel();
        column.setId(id);
        columns.add(column);
        return column;
    }

    public void addColumns(List<ColumnModel> requiredColumns) {
        columns.addAll(requiredColumns);
    }

    /**
     * Adds the {@code ResourceId} as a string column to the table model with
     * the given column id
     */
    public TableModel addResourceId(String columnId) {
        ColumnModel columnModel = new ColumnModel();
        columnModel.setId(columnId);
        columnModel.selectId();
        columns.add(columnModel);
        return this;
    }

    @Override
    public Record asRecord() {
        Record record = new Record();
        record.set("rowSources", Records.toRecordList(rowSources));
        record.set("columnModels", Records.toRecordList(columns));
        return record;
    }

    public static TableModel fromRecord(Record record) {
        TableModel model = new TableModel();

        for(Record sourceRecord : record.getRecordList("rowSources")) {
            model.rowSources.add(RowSource.fromRecord(sourceRecord));
        }
        for(Record columnRecord : record.getRecordList("columnModels")) {
            model.columns.add(ColumnModel.fromRecords(columnRecord));
        }
        return model;
    }


}

