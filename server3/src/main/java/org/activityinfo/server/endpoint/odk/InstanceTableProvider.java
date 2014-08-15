package org.activityinfo.server.endpoint.odk;

import com.google.inject.Inject;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.system.ApplicationProperties;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.service.store.ResourceStore;
import org.activityinfo.ui.client.component.form.field.InstanceLabelTable;

import javax.inject.Provider;

public class InstanceTableProvider {

    private final ResourceStore store;
    private final Provider<AuthenticatedUser> user;

    @Inject
    public InstanceTableProvider(ResourceStore store, Provider<AuthenticatedUser> user) {
        this.user = user;
        this.store = store;
    }

    public InstanceLabelTable getTable(ResourceId formClassId) {

        TableModel tableModel = new TableModel(formClassId);
        tableModel.addResourceId("id");
        tableModel.addColumn("label").select().fieldPath(ApplicationProperties.LABEL_PROPERTY);

        TableData table = store.queryTable(user.get(), tableModel);

        return new InstanceLabelTable(table.getColumnView("id"), table.getColumnView("label"));
    }
}
