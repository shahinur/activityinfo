package org.activityinfo.ui.client.service;

import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.model.table.TableServiceAsync;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.store.ResourceStore;

public class TestTableServiceAsync implements TableServiceAsync {

    private final ResourceStore tableService;

    public TestTableServiceAsync(ResourceStore tableService) {
        this.tableService = tableService;
    }

    @Override
    public Promise<TableData> query(TableModel tableModel) {
        try {
            return Promise.resolved(tableService.queryTable(AuthenticatedUser.getAnonymous(), tableModel));
        } catch(Exception e) {
            return Promise.rejected(e);
        }
    }
}
