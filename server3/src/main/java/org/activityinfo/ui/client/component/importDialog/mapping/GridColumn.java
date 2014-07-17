package org.activityinfo.ui.client.component.importDialog.mapping;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import org.activityinfo.core.shared.importing.source.SourceColumn;
import org.activityinfo.core.shared.importing.source.SourceRow;

class GridColumn extends Column<SourceRow, String> {
    private SourceColumn column;

    public GridColumn(SourceColumn column) {
        super(new TextCell());
        this.column = column;
    }

    @Override
    public String getValue(SourceRow row) {
        return row.getColumnValue(column.getIndex());
    }
}
