package org.activityinfo.service.store;

import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.resource.ResourceTree;
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
     * Creates or updates a Response
     */
    Promise<UpdateResult> put(Resource resource);

    /**
     * Retrieves the root resources that are owned or have been shared by the user
     */
    Promise<List<ResourceNode>> queryRoots();

    Promise<TableData> queryTable(TableModel tableModel);

    Promise<ResourceTree> queryTree(ResourceId rootId);
}
