package org.activityinfo.legacy.shared.adapter;

import com.google.common.collect.Lists;
import org.activityinfo.core.client.InstanceQuery;
import org.activityinfo.core.client.QueryResult;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.criteria.ClassCriteria;
import org.activityinfo.core.shared.criteria.Criteria;
import org.activityinfo.core.shared.criteria.IdCriteria;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.legacy.client.Dispatcher;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.IsResource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.promise.Promise;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Exposes a legacy {@code Dispatcher} implementation as new {@code ResourceLocator}
 */
public class ResourceLocatorAdaptor implements ResourceLocator {

    private final Dispatcher dispatcher;
    private final ClassProvider classProvider;

    public ResourceLocatorAdaptor(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
        this.classProvider = new ClassProvider(dispatcher);
    }

    @Override
    public Promise<FormClass> getFormClass(ResourceId classId) {
        return classProvider.apply(classId);
    }

    @Override
    public Promise<FormInstance> getFormInstance(ResourceId instanceId) {
        return queryInstances(new IdCriteria(instanceId)).then(new SelectSingle());
    }

    @Override
    public Promise<Void> persist(IsResource resource) {
        if (resource instanceof FormInstance) {
            FormInstance instance = (FormInstance) resource;
            if (instance.getId().getDomain() == CuidAdapter.SITE_DOMAIN) {
                return new SitePersister(dispatcher).persist(instance);

            } else if (instance.getId().getDomain() == CuidAdapter.LOCATION_DOMAIN) {
                return new LocationPersister(dispatcher, instance).persist();
            }
        } else if(resource instanceof FormClass) {
            return new FormPersister(dispatcher, (FormClass)resource).persist();
        }
        return Promise.rejected(new UnsupportedOperationException());
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
        return new QueryExecutor(dispatcher, criteria).execute();
    }

    @Override
    public Promise<List<Projection>> query(InstanceQuery query) {
        return new Joiner(dispatcher, query.getFieldPaths(), query.getCriteria()).apply(query);
    }

    public Promise<QueryResult<Projection>> queryProjection(InstanceQuery query) {
        return query(query).then(new InstanceQueryResultAdapter(query));
    }


    @Override
    public Promise<Void> remove(Collection<ResourceId> resources) {
        return new Eraser(dispatcher, resources).execute();
    }

    @Override
    public Promise<List<FormInstance>> queryInstances(Set<ResourceId> formClassIds) {
        return queryInstances(ClassCriteria.union(formClassIds));
    }
}
