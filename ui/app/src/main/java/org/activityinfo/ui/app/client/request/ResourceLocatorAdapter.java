package org.activityinfo.ui.app.client.request;

import com.google.common.base.Function;
import org.activityinfo.client.ActivityInfoAsyncClient;
import org.activityinfo.client.ProjectionAdapter;
import org.activityinfo.client.ResourceLocator;
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
import org.activityinfo.ui.app.client.Application;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@SuppressWarnings("deprecation")
public class ResourceLocatorAdapter implements ResourceLocator {

    private final Application application;
    private final ActivityInfoAsyncClient remoteSevice;
    private RequestDispatcher requestDispatcher;

    public ResourceLocatorAdapter(Application application) {
        this.application = application;
        this.requestDispatcher = application.getRequestDispatcher();
        this.remoteSevice = application.getRemoteService();
    }

    @Override
    public Promise<FormClass> getFormClass(ResourceId formId) {
        return requestDispatcher.execute(new FetchResource(formId)).then(new Function<UserResource, FormClass>() {
            @Override
            public FormClass apply(UserResource input) {
                return FormClass.fromResource(input.getResource());
            }
        });
    }

    @Override
    public Promise<FormInstance> getFormInstance(ResourceId formId) {
        return requestDispatcher.execute(new FetchResource(formId)).then(new Function<UserResource, FormInstance>() {
            @Override
            public FormInstance apply(UserResource input) {
                return FormInstance.fromResource(input.getResource());
            }
        });
    }

    @Override
    public Promise<List<UserResource>> get(Set<ResourceId> resourceIds) {
        return Promise.map(resourceIds, new Function<ResourceId, Promise<UserResource>>() {
            @Override
            public Promise<UserResource> apply(ResourceId input) {
                return requestDispatcher.execute(new FetchResource(input));
            }
        });
    }

    @Override
    public Promise<TableData> queryTable(TableModel tableModel) {
        return remoteSevice.queryTable(tableModel);
    }

    @Override
    public Promise<Void> persist(IsResource resource) {
        return requestDispatcher.execute(new SaveRequest(resource)).thenDiscardResult();
    }

    @Override
    public Promise<Void> persist(List<? extends IsResource> resources) {
        return Promise.forEach(resources, new Function<IsResource, Promise<Void>>() {
            @Nullable
            @Override
            public Promise<Void> apply(@Nullable IsResource input) {
                return persist(input);
            }
        });
    }

    @Override
    public Promise<List<Projection>> query(InstanceQuery query) {
        return new ProjectionAdapter(application.getRemoteService()).query(query).then(new Function<QueryResult, List<Projection>>() {
            @Override
            public List<Projection> apply(QueryResult input) {
                return input.getProjections();
            }
        });
    }

    @Override
    public Promise<Void> remove(Collection<ResourceId> resources) {
        return Promise.forEach(resources, new Function<ResourceId, Promise<Void>>() {
            @Nullable
            @Override
            public Promise<Void> apply(@Nullable ResourceId input) {
                return requestDispatcher.execute(new RemoveRequest(input)).thenDiscardResult();
            }
        });
    }

    @Override
    public Promise<QueryResult> queryProjection(InstanceQuery query) {
        return new ProjectionAdapter(application.getRemoteService()).query(query);
    }

    @Override
    public Promise<InstanceLabelTable> queryFormList() {
        //TODO
        return Promise.rejected(new UnsupportedOperationException());
    }
}
