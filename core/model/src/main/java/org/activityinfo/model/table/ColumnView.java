package org.activityinfo.model.table;

import java.util.Date;

public interface ColumnView {

    ColumnType getType();

    int numRows();

    Object get(int row);

    double getDouble(int row);

    String getString(int row);

    Date getDate(int row);
}
