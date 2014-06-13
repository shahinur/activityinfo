package org.activityinfo.core.client;


import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.Resource;
import org.activityinfo.core.shared.criteria.Criteria;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.fp.client.Promise;

import java.util.Collection;
import java.util.List;

public interface ResourceLocator {

    /**
     * Fetches the user form.
     *
     * @param formId
     * @return
     */
    Promise<FormClass> getFormClass(Cuid formId);

    Promise<FormInstance> getFormInstance(Cuid formId);

    /**
     * Persists a resource to the server, creating or updating as necessary.
     *
     * @param resource the resource to persist.
     * @return a Promise that resolves when the persistance operation completes
     * successfully.
     */
    Promise<Void> persist(Resource resource);

    Promise<Void> persist(List<? extends Resource> resources);

    /**
     * Retrieves the form instances that match the given criteria.
     * @param criteria
     */
    Promise<List<FormInstance>> queryInstances(Criteria criteria);

    Promise<QueryResult<Projection>> queryProjection(InstanceQuery query);

    Promise<List<Projection>> query(InstanceQuery query);

    Promise<Void> remove(Collection<Cuid> resources);
}
