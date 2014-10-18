package org.activityinfo.client;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.legacy.InstanceQuery;
import org.activityinfo.model.legacy.Projection;
import org.activityinfo.model.legacy.QueryResult;
import org.activityinfo.model.resource.IsResource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.UserResource;
import org.activityinfo.model.system.ApplicationClassProvider;
import org.activityinfo.model.table.ColumnSet;
import org.activityinfo.model.table.InstanceLabelTable;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.promise.Promise;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Exposes a legacy {@code Dispatcher} implementation as new {@code ResourceLocator}
 */
public class LocatorAdapter implements ResourceLocator {

    private final ActivityInfoAsyncClient client;
    private final ApplicationClassProvider systemClassProvider = new ApplicationClassProvider();
    private final ProjectionAdapter projectionAdapter;

    public LocatorAdapter(ActivityInfoAsyncClient client) {
        this.client = client;
        this.projectionAdapter = new ProjectionAdapter(client);
    }

    @Override
    public Promise<FormClass> getFormClass(ResourceId classId) {
        if(classId.asString().startsWith("_")) {
            return Promise.resolved(systemClassProvider.get(classId));
        } else {
            return client.get(classId).then(new Function<UserResource, FormClass>() {
                @Nullable
                @Override
                public FormClass apply(@Nullable UserResource input) {
                    return FormClass.fromResource(input.getResource());
                }
            });
        }
    }

    @Override
    public Promise<FormInstance> getFormInstance(ResourceId instanceId) {
        return client.get(instanceId).then(new Function<UserResource, FormInstance>() {
            @Nullable
            @Override
            public FormInstance apply(@Nullable UserResource input) {
                return FormInstance.fromResource(input.getResource());
            }
        });
    }

    @Override
    public Promise<List<UserResource>> get(Set<ResourceId> resourceIds) {
        return Promise.map(resourceIds, new Function<ResourceId, Promise<UserResource>>() {
            @Override
            public Promise<UserResource> apply(ResourceId input) {
                return client.get(input);
            }
        });
    }

    @Override
    public Promise<ColumnSet> queryTable(TableModel tableModel) {
        return client.queryColumns(tableModel);
    }

    @Override
    public Promise<Void> persist(IsResource resource) {
        return client.put(resource.asResource()).thenDiscardResult();
    }

    @Override
    public Promise<Void> persist(List<? extends IsResource> resources) {
        final List<Promise<Void>> promises = Lists.newArrayList();
        if (resources != null && !resources.isEmpty()) {
            for (final IsResource resource : resources) {
                promises.add(persist(resource));
            }
        }
        return Promise.waitAll(promises);
    }

    @Override
    public Promise<QueryResult> queryProjection(InstanceQuery query) {
        return projectionAdapter.query(query);
    }

    @Override
    public Promise<List<Projection>> query(InstanceQuery query) {
        return projectionAdapter.query(query).then(new Function<QueryResult, List<Projection>>() {
            @Override
            public List<Projection> apply(QueryResult input) {
                return input.getProjections();
            }
        });
    }

    @Override
    public Promise<Void> remove(Collection<ResourceId> resources) {
        List<Promise<?>> promises = Lists.newArrayList();
        for (ResourceId resource : resources) {
            promises.add(client.remove(resource));
        }
        return Promise.waitAll(promises);
    }

    @Override
    public Promise<InstanceLabelTable> queryFormList() {
        throw new UnsupportedOperationException();
    }
}
