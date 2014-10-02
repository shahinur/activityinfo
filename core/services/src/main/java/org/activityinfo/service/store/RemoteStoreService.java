package org.activityinfo.service.store;

import org.activityinfo.model.analysis.PivotTableModel;
import org.activityinfo.model.resource.FolderProjection;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.resource.UserResource;
import org.activityinfo.model.table.Bucket;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.tasks.UserTask;

import java.util.List;

public interface RemoteStoreService {


    /**
     * Retrieves a single Resource from the server.
     */
    Promise<UserResource> get(ResourceId resourceId);

    /**
     * Updates a Response
     */
    Promise<UpdateResult> put(Resource resource);

    /**
     * Creates a new Response
     */
    Promise<UpdateResult> create(Resource resource);

    /**
     * Starts an import of a datafile to a Form
     */
    Promise<UserTask> startImport(ResourceId ownerId, BlobId blobId);

    /**
     *
     * @return a list of tasks running on behalf of the user
     */
    Promise<List<UserTask>> getTasks();

    /**
     * Retrieves the root resources that are owned or have been shared by the user
     */
    Promise<List<ResourceNode>> getWorkspaces();

    Promise<TableData> queryTable(TableModel tableModel);

    Promise<List<Bucket>> queryCube(PivotTableModel cubeModel);

    Promise<FolderProjection> getFolder(ResourceId rootId);

    /**
     * Removes resources from store by respective id.
     */
    Promise<UpdateResult> remove(ResourceId resourceId);
}
