package org.activityinfo.service.tables;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.table.columns.StringArrayColumnView;

import java.util.List;

/**
 * Supplies a Column that is combined from several source columns.
 *
 */
class ColumnCombiner implements Supplier<ColumnView> {

    private ColumnType type;
    private List<Supplier<ColumnView>> sources;

    private ColumnView result;

    ColumnCombiner(ColumnType type, List<Supplier<ColumnView>> sources) {
        Preconditions.checkNotNull(type, "type");
        Preconditions.checkArgument(sources.size() > 1, "source.size() > 1");
        this.type = type;
        this.sources = sources;
    }

    @Override
    public ColumnView get() {
        if(result == null) {
            result = combine();
        }
        return result;
    }

    private ColumnView combine() {
        if(type == ColumnType.STRING) {
            return combineString();
        }
        throw new UnsupportedOperationException();
    }

    private ColumnView combineString() {
        ColumnView[] cols = new ColumnView[sources.size()];
        for(int j=0;j<cols.length;++j) {
            cols[j] = sources.get(j).get();
        }
        int numRows = cols[0].numRows();
        int numCols = cols.length;

        String[] values = new String[numRows];

        for(int i=0;i!=numRows;++i) {
            for(int j=0;j!=numCols;++j) {
                String value = cols[j].getString(j);
                if(value != null) {
                    values[i] = value;
                    break;
                }
            }
        }

        return new StringArrayColumnView(values);
    }


}
