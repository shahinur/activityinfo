package org.activityinfo.model.table;

import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.table.columns.EmptyColumnView;

/**
 * A table containing an column of resource ids, and a label column for a set of
 * instances.
 */
public class InstanceLabelTable {
    private final ColumnView idColumn;
    private final ColumnView labelColumn;

    public InstanceLabelTable(ColumnView idColumn, ColumnView labelColumn) {
        this.idColumn = idColumn;
        this.labelColumn = labelColumn;
    }

    public int getNumRows() {
        return idColumn.numRows();
    }

    public ResourceId getId(int rowIndex) {
        return ResourceId.valueOf(idColumn.getString(rowIndex));
    }

    public String getLabel(int rowIndex) {
        return labelColumn.getString(rowIndex);
    }

    public static InstanceLabelTable empty() {
        return new InstanceLabelTable(new EmptyColumnView(0, ColumnType.STRING),
                                      new EmptyColumnView(0, ColumnType.STRING));
    }
}
