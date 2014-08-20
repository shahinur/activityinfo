package org.activityinfo.ui.component.importDialog.validation;

import com.google.gwt.user.cellview.client.Column;
import org.activityinfo.io.importing.strategy.ColumnAccessor;
import org.activityinfo.io.importing.validation.ValidatedRow;
import org.activityinfo.ui.component.importDialog.validation.cells.ValidationResultCell;

public class ValidationRowGridColumn extends Column<ValidatedRow, ValidatedRow> {

    /**
     * Construct a new Column with a given {@link com.google.gwt.cell.client.Cell}.
     *
     * @param accessor
     */
    public ValidationRowGridColumn(ColumnAccessor accessor, int columnIndex) {
        super(new ValidationResultCell(accessor, columnIndex));
    }

    @Override
    public ValidatedRow getValue(ValidatedRow row) {
        return row;
    }
}
