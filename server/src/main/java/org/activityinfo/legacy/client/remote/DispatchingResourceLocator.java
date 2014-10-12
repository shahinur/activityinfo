package org.activityinfo.legacy.client.remote;

import org.activityinfo.legacy.client.Dispatcher;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.legacy.InstanceQuery;
import org.activityinfo.model.legacy.Projection;
import org.activityinfo.model.legacy.QueryResult;
import org.activityinfo.model.resource.IsResource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.UserResource;
import org.activityinfo.model.table.InstanceLabelTable;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.store.ResourceLocator;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by alex on 10/12/14.
 */
public class DispatchingResourceLocator implements ResourceLocator {
    public DispatchingResourceLocator(Dispatcher dispatcher) {

    }

    @Override
    public Promise<FormClass> getFormClass(ResourceId formId) {
        return Promise.rejected(new UnsupportedOperationException());
    }

    @Override
    public Promise<FormInstance> getFormInstance(ResourceId formId) {
        return Promise.rejected(new UnsupportedOperationException());
    }

    @Override
    public Promise<List<UserResource>> get(Set<ResourceId> resourceIds) {
        return Promise.rejected(new UnsupportedOperationException());
    }

    @Override
    public Promise<TableData> queryTable(TableModel tableModel) {
        return Promise.rejected(new UnsupportedOperationException());
    }

    @Override
    public Promise<Void> persist(IsResource resource) {
        return Promise.rejected(new UnsupportedOperationException());
    }

    @Override
    public Promise<Void> persist(List<? extends IsResource> resources) {
        return Promise.rejected(new UnsupportedOperationException());
    }

    @Override
    public Promise<List<Projection>> query(InstanceQuery query) {
        return Promise.rejected(new UnsupportedOperationException());
    }

    @Override
    public Promise<Void> remove(Collection<ResourceId> resources) {
        return Promise.rejected(new UnsupportedOperationException());
    }

    @Override
    public Promise<QueryResult> queryProjection(InstanceQuery query) {
        return Promise.rejected(new UnsupportedOperationException());
    }

    @Override
    public Promise<InstanceLabelTable> queryFormList() {
        return Promise.rejected(new UnsupportedOperationException());
    }
}
