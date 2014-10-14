package org.activityinfo.service.tables.views;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.service.tables.QuerySyntaxException;

import java.util.Arrays;


public class ColumnFilter implements Function<ColumnView, ColumnView> {

    private final Supplier<ColumnView> filterColumn;
    private int[] includeMap;

    /**
     * @param filterColumn supplier of a boolean-valued column that indicates which rows should
     *                   be included in the result set
     */
    public ColumnFilter(Supplier<ColumnView> filterColumn) {
        this.filterColumn = filterColumn;
    }

    private void computeIndices() {
        if(includeMap == null) {
            ColumnView view = filterColumn.get();
            if (view.getType() != ColumnType.BOOLEAN) {
                throw new QuerySyntaxException("Filter expression must evaluate to a boolean-valued column");
            }
            int[] filtered = new int[view.numRows()];
            int filteredRowIndex = 0;
            for (int rowIndex = 0; rowIndex != view.numRows(); ++rowIndex) {
                if (view.getBoolean(rowIndex) == ColumnView.TRUE) {
                    filtered[filteredRowIndex++] = rowIndex;
                }
            }
            int numFilteredRows = filteredRowIndex;
            includeMap = Arrays.copyOf(filtered, numFilteredRows);
        }
    }

    @Override
    public ColumnView apply(ColumnView input) {
        computeIndices();
        return new FilteredColumnView(input, includeMap);
    }
}
