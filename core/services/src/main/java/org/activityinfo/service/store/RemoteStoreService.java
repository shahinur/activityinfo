package org.activityinfo.service.store;

import org.activityinfo.model.resource.FolderProjection;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.promise.Promise;

import java.util.List;

public interface RemoteStoreService {


    /**
     * Retrieves a single Resource from the server.
     */
    Promise<Resource> get(ResourceId resourceId);

    /**
     * Updates a Response
     */
    Promise<UpdateResult> put(Resource resource);

    /**
     * Creates a new Response
     */
    Promise<UpdateResult> create(Resource resource);


    /**
     * Retrieves the root resources that are owned or have been shared by the user
     */
    Promise<List<ResourceNode>> getWorkspaces();

    Promise<TableData> queryTable(TableModel tableModel);

    Promise<FolderProjection> getFolder(ResourceId rootId);
}
