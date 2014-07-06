package org.activityinfo.model.table;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Describes a Table to be constructed from a
 * FormTree.
 */
public class TableModel {

    private final List<RowSource> rowSources = Lists.newArrayList();
    private final List<ColumnModel> columns = Lists.newArrayList();

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
}

