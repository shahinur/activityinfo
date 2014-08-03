package org.activityinfo.ui.client.service;

import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.model.table.TableService;
import org.activityinfo.model.table.TableServiceAsync;
import org.activityinfo.promise.Promise;

public class TestTableServiceAsync implements TableServiceAsync {

    private final TableService tableService;

    public TestTableServiceAsync(TableService tableService) {
        this.tableService = tableService;
    }

    @Override
    public Promise<TableData> query(TableModel tableModel) {
        try {
            return Promise.resolved(tableService.buildTable(tableModel));
        } catch(Exception e) {
            return Promise.rejected(e);
        }
    }
}
