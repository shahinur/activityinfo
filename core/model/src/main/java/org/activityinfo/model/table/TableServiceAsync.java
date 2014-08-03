package org.activityinfo.model.table;

import org.activityinfo.promise.Promise;

public interface TableServiceAsync {

    Promise<TableData> query(TableModel tableModel);
}
