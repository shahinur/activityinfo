package org.activityinfo.client;


import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.legacy.InstanceQuery;
import org.activityinfo.model.legacy.Projection;
import org.activityinfo.model.legacy.QueryResult;
import org.activityinfo.model.resource.IsResource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.UserResource;
import org.activityinfo.model.table.ColumnSet;
import org.activityinfo.model.table.InstanceLabelTable;
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

    Promise<List<UserResource>> get(Set<ResourceId> resourceIds);

    Promise<ColumnSet> queryTable(TableModel tableModel);

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
    Promise<List<Projection>> query(InstanceQuery query);

    Promise<Void> remove(Collection<ResourceId> resources);

    Promise<QueryResult> queryProjection(InstanceQuery query);

    Promise<InstanceLabelTable> queryFormList();

}
