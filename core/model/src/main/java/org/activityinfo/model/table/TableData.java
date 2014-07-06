package org.activityinfo.model.table;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Contains the data
 */
public class TableData {

    private final int numRows;
    private final Map<String, ColumnView> columnViews;

    public TableData(int numRows, @Nonnull Map<String, ColumnView> columnViews) {
        this.numRows = numRows;
        this.columnViews = columnViews;
    }

    /**
     * @return the number of rows in this Table
     */
    public int getNumRows() {
        return numRows;
    }


    @Nonnull
    public Map<String, ColumnView> getColumns() {
        return columnViews;
    }

    /**
     *
     * @param id the {@code id} of the {@code ColumnModel}
     * @return the {@code ColumnView} generated from the given {@code ColumnModel}
     */
    public ColumnView getColumnView(String columnModelId) {
        return columnViews.get(columnModelId);
    }

}
