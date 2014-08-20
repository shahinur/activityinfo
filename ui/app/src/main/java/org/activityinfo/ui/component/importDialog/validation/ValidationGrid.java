package org.activityinfo.ui.component.importDialog.validation;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.ResizeComposite;
import org.activityinfo.io.importing.strategy.FieldImporterColumn;
import org.activityinfo.io.importing.validation.ValidatedRow;
import org.activityinfo.io.importing.validation.ValidatedRowTable;
import org.activityinfo.ui.widget.grid.ColumnWidths;
import org.activityinfo.ui.widget.grid.DataGridResources;

/**
 * A second DataGrid that allows the user to the resolve
 * any problems before import
 */
public class ValidationGrid extends ResizeComposite {


    private DataGrid<ValidatedRow> dataGrid;

    public ValidationGrid() {
        this.dataGrid = new DataGrid<>(100, DataGridResources.INSTANCE);
        initWidget(dataGrid);
        this.dataGrid.setWidth("100%");
        this.dataGrid.setHeight("100%");
    }

    public void refresh(ValidatedRowTable table) {
        while (dataGrid.getColumnCount() > 0) {
            dataGrid.removeColumn(0);
        }
        for(int i = 0; i< table.getColumns().size(); i++) {
            final FieldImporterColumn column = table.getColumns().get(i);
            String columnHeader = column.getAccessor().getHeading();
            dataGrid.addColumn(new ValidationRowGridColumn(column.getAccessor(), i),
                    new TextHeader(columnHeader));
            dataGrid.setColumnWidth(i, ColumnWidths.columnWidthInEm(columnHeader), Style.Unit.EM);
        }
        dataGrid.setRowData(table.getRows());
    }

    public int getInvalidRowsCount() {
        int invalidRowsCount = 0;
        for (ValidatedRow row : dataGrid.getVisibleItems()) {
            if (!row.isValid()) {
                invalidRowsCount++;
            }
        }
        return invalidRowsCount;
    }


}
