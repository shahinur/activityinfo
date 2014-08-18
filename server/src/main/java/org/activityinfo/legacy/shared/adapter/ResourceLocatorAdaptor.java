package org.activityinfo.legacy.shared.adapter;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.activityinfo.core.client.InstanceQuery;
import org.activityinfo.core.client.QueryResult;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.criteria.Criteria;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.system.ApplicationClassProvider;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.store.remote.client.RemoteStoreService;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Exposes a legacy {@code Dispatcher} implementation as new {@code ResourceLocator}
 */
public class ResourceLocatorAdaptor implements ResourceLocator {

    private final ApplicationClassProvider systemClassProvider = new ApplicationClassProvider();


    private final RemoteStoreService store;

    private final ProjectionAdapter projectionAdapter;

    public ResourceLocatorAdaptor(RemoteStoreService tableService) {
        this.store = tableService;
        this.projectionAdapter = new ProjectionAdapter(tableService);
    }

    @Override
    public Promise<FormClass> getFormClass(ResourceId classId) {
        if(classId.asString().startsWith("_")) {
            return Promise.resolved(systemClassProvider.get(classId));
        } else {
            return store.get(classId).then(new Function<Resource, FormClass>() {
                @Nullable
                @Override
                public FormClass apply(@Nullable Resource input) {
                    return FormClass.fromResource(input);
                }
            });
        }
    }

    @Override
    public Promise<FormInstance> getFormInstance(ResourceId instanceId) {
        return store.get(instanceId).then(new Function<Resource, FormInstance>() {
            @Nullable
            @Override
            public FormInstance apply(@Nullable Resource input) {
                return FormInstance.fromResource(input);
            }
        });
    }

    @Override
    public Promise<List<Resource>> get(Set<ResourceId> resourceIds) {
        return Promise.map(resourceIds, new Function<ResourceId, Promise<Resource>>() {
            @Override
            public Promise<Resource> apply(ResourceId input) {
                return store.get(input);
            }
        });
    }

    @Override
    public Promise<TableData> queryTable(TableModel tableModel) {
        return store.queryTable(tableModel);
    }

    @Override
    public Promise<List<ResourceNode>> getRoots() {
        return store.queryRoots();
    }

    @Override
    public Promise<Void> persist(IsResource resource) {
        return store.put(resource.asResource()).thenDiscardResult();
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
    public Promise<List<FormInstance>> queryInstances(Criteria criteria) {
        throw new UnsupportedOperationException("deprecated");
    }

    @Override
    public Promise<QueryResult> queryProjection(InstanceQuery query) {
        return projectionAdapter.query(query);
    }

    @Override
    public Promise<ResourceTree> getTree(final ResourceId rootId) {
        return store.queryTree(rootId);
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
        throw new UnsupportedOperationException("todo");
    }
}
