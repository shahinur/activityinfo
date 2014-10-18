package org.activityinfo.client;

import org.activityinfo.model.analysis.PivotTableModel;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.table.Bucket;
import org.activityinfo.model.table.ColumnSet;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.store.UpdateResult;
import org.activityinfo.service.tasks.UserTask;

import java.util.List;

public interface ActivityInfoAsyncClient {


    /**
     * Retrieves a single Resource from the server.
     */
    Promise<UserResource> get(ResourceId resourceId);

    /**
     * Updates a Response
     */
    Promise<UpdateResult> put(Resource resource);

    String getBlobDownloadUrl(BlobId blobId);

    /**
     * Creates a new Response
     */
    Promise<UpdateResult> create(Resource resource);

    /**
     *
     * @return a list of tasks running on behalf of the user
     */
    Promise<List<UserTask>> getTasks();

    /**
     * Initiates a user task
     *
     * @param taskModel a record describing the task to be performed.
     * For example: {@link org.activityinfo.service.tasks.ExportFormTaskModel}
     * @return the initial status of the task.
     */
    Promise<UserTask> startTask(Record taskModel);

    /**
     * Initiates a user task, and polls the server until it has been completed.
     * @param taskModel a record describing the task to be performed.
     * For example: {@link org.activityinfo.service.tasks.ExportFormTaskModel}
     * @return the final result of this task.
     */
    Promise<UserTask> executeTask(Record taskModel);

    /**
     * Retrieves the root resources that are owned or have been shared by the user
     */
    Promise<List<ResourceNode>> getWorkspaces();

    Promise<ColumnSet> queryColumns(TableModel tableModel);

    Promise<List<Bucket>> queryCube(PivotTableModel cubeModel);

    Promise<FolderProjection> getFolder(ResourceId rootId);

    /**
     * Removes resources from store by respective id.
     */
    Promise<UpdateResult> remove(ResourceId resourceId);

    /**
     * Tests the client's connection to the server.
     */
    Promise<Void> ping();
}
