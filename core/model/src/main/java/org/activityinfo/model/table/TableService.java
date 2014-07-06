package org.activityinfo.model.table;

/**
 * Interface to a synchronous service which provides
 * TableData for TableModels
 */
public interface TableService {

    TableData buildTable(TableModel tableModel);

}
