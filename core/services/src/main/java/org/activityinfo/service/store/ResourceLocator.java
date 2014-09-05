package org.activityinfo.service.store;


import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.legacy.InstanceQuery;
import org.activityinfo.model.legacy.Projection;
import org.activityinfo.model.legacy.QueryResult;
import org.activityinfo.model.legacy.criteria.Criteria;
import org.activityinfo.model.resource.FolderProjection;
import org.activityinfo.model.resource.IsResource;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.table.InstanceLabelTable;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.promise.Promise;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ResourceLocator {

    /**
     * Fetches the user form.
     *
     * @param formId
     * @return
     */
    Promise<FormClass> getFormClass(ResourceId formId);

    Promise<FormInstance> getFormInstance(ResourceId formId);

    Promise<List<Resource>> get(Set<ResourceId> resourceIds);

    Promise<TableData> queryTable(TableModel tableModel);

    /**
     * Persists a resource to the server, creating or updating as necessary.
     *
     * @param resource the resource to persist.
     * @return a Promise that resolves when the persistance operation completes
     * successfully.
     */
    Promise<Void> persist(IsResource resource);

    Promise<Void> persist(List<? extends IsResource> resources);

    /**
     * @deprecated Use {@link ResourceLocator#queryTable(org.activityinfo.model.table.TableModel)} instead
     */
    @Deprecated
    Promise<List<FormInstance>> queryInstances(Criteria criteria);

    /**
     * @deprecated Use {@link ResourceLocator#queryTable(org.activityinfo.model.table.TableModel)} instead
     */
    @Deprecated
    Promise<List<Projection>> query(InstanceQuery query);

    Promise<Void> remove(Collection<ResourceId> resources);

    Promise<QueryResult> queryProjection(InstanceQuery query);

    Promise<FolderProjection> getTree(ResourceId rootId);

    Promise<List<org.activityinfo.model.resource.ResourceNode>> getRoots();

    Promise<InstanceLabelTable> queryFormList();

}
