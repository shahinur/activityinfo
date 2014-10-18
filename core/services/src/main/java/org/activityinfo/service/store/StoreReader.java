package org.activityinfo.service.store;

import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.resource.UserResource;
import org.activityinfo.model.table.ColumnSet;
import org.activityinfo.model.table.TableModel;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface to the ResourceStore that offers a consistent, authorized
 * view of the store for a given user
 */
public interface StoreReader extends AutoCloseable {

    UserResource getResource(ResourceId resourceId);

    ResourceNode getResourceNode(ResourceId resourceId);

    Iterable<ResourceNode> getFolderItems(ResourceId parentId);

    FormTree getFormTree(ResourceId formClassId);

    Map<ResourceId, UserResource> getResources(Set<ResourceId> resourceIds);

    ColumnSet queryColumns(TableModel tableModel);

    ResourceCursor openCursor(ResourceId formClassId) throws Exception;

    List<ResourceNode> getOwnedOrSharedWorkspaces();

    @Override
    void close();
}
