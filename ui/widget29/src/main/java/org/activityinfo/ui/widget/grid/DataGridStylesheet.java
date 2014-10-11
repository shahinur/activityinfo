package org.activityinfo.ui.widget.grid;

import com.google.gwt.user.cellview.client.DataGrid;

/**
 * Application style sheet for the Data Grid
 */
public class DataGridStylesheet implements DataGrid.Style {
    @Override
    public String dataGridCell() { return "data-grid-cell"; }

    @Override
    public String dataGridEvenRow() { return "data-grid-even-row"; }

    @Override
    public String dataGridEvenRowCell() { return "data-grid-even-row-cell"; }

    @Override
    public String dataGridFirstColumn() { return "data-grid-first-Column"; }

    @Override
    public String dataGridFirstColumnFooter() { return "data-grid-first-column-footer"; }

    @Override
    public String dataGridFirstColumnHeader() {
        return "data-grid-first-column-header";
    }

    @Override public String dataGridFooter () {
        return "data-grid-footer";
    }

    @Override public String dataGridHeader () {
        return "data-grid-header";
    }

    @Override public String dataGridHoveredRow () {
        return "data-grid-hovered-row";
    }

    @Override public String dataGridHoveredRowCell () {
        return "data-grid-hovered-row-cell";
    }

    @Override public String dataGridKeyboardSelectedCell () {
        return "data-grid-keyboard-selected-cell";
    }

    @Override public String dataGridKeyboardSelectedRow () {
        return "data-grid-keyboard-selected-row";
    }

    @Override public String dataGridKeyboardSelectedRowCell () {
        return "data-grid-keyboard-selected-row-cell";
    }

    @Override public String dataGridLastColumn () {
        return "data-grid-last-column";
    }

    @Override public String dataGridLastColumnFooter () {
        return "data-grid-last-column-footer";
    }

    @Override public String dataGridLastColumnHeader () {
        return "data-grid-last-column-header";
    }

    @Override public String dataGridOddRow () {
        return "data-grid-odd-row";
    }

    @Override public String dataGridOddRowCell () {
        return "data-grid-odd-row-cell";
    }

    @Override public String dataGridSelectedRow () {
        return "data-grid-selected-row";
    }

    @Override public String dataGridSelectedRowCell () {
        return "data-grid-selected-row-cell";
    }

    @Override public String dataGridSortableHeader () {
        return "data-grid-sortable-header";
    }

    @Override public String dataGridSortedHeaderAscending () {
        return "data-grid-sorted-header-ascending";
    }

    @Override public String dataGridSortedHeaderDescending () {
        return "data-grid-sorted-header-descending";
    }

    @Override public String dataGridWidget () {
        return "data-grid-widget";
    }

    @Override public boolean ensureInjected () {
        return true;
    }

    @Override public String getText () {
        throw new UnsupportedOperationException();
    }

    @Override public String getName () {
        return "FilterDataGridStyles";
    }
}