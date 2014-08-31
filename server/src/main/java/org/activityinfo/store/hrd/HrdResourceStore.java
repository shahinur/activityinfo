package org.activityinfo.store.hrd;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.common.collect.Sets;
import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.resource.ResourceTree;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.service.store.ResourceNotFound;
import org.activityinfo.service.store.ResourceStore;
import org.activityinfo.service.store.ResourceTreeRequest;
import org.activityinfo.service.store.UpdateResult;

import javax.ws.rs.PathParam;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HrdResourceStore implements ResourceStore {

    private final DatastoreService datastore;
    private final DatastoreMapper mapper;

    public HrdResourceStore(DatastoreService datastore) {
        this.datastore = datastore;
        this.mapper = new DatastoreMapper();
    }

    @Override
    public Resource get(@InjectParam AuthenticatedUser user, @PathParam("id") ResourceId resourceId) {
        try {
            return mapper.toResource(datastore.get(DatastoreMapper.resourceKey(resourceId)));
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFound(resourceId);
        }
    }

    @Override
    public List<Resource> getAccessControlRules(@InjectParam AuthenticatedUser user,
                                                @PathParam("id") ResourceId resourceId) {
        return null;
    }

    @Override
    public Set<Resource> get(@InjectParam AuthenticatedUser user, Set<ResourceId> resourceIds) {
        Iterable<Key> keys = mapper.resourceKeys(resourceIds);
        Map<Key, Entity> entities = datastore.get(keys);
        return Sets.newHashSet(mapper.toResources(entities.values()));
    }

    @Override
    public UpdateResult put(@InjectParam AuthenticatedUser user,
                            @PathParam("id") ResourceId resourceId,
                            Resource resource) {
        datastore.put(mapper.toEntity(resource));
    }

    @Override
    public UpdateResult put(AuthenticatedUser user, Resource resource) {
        return null;
    }

    @Override
    public UpdateResult create(AuthenticatedUser user, Resource resource) {
        return null;
    }

    @Override
    public ResourceTree queryTree(@InjectParam AuthenticatedUser user, ResourceTreeRequest request) {
        return null;
    }

    @Override
    public TableData queryTable(@InjectParam AuthenticatedUser user, TableModel tableModel) {
        return null;
    }

    @Override
    public List<ResourceNode> getOwnedOrSharedWorkspaces(@InjectParam AuthenticatedUser user) {
        return null;
    }
}
