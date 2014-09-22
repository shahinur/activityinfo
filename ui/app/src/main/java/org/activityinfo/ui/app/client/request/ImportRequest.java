package org.activityinfo.ui.app.client.request;

import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.store.RemoteStoreService;
import org.activityinfo.service.tasks.UserTask;

public class ImportRequest implements Request<UserTask> {

    private ResourceId ownerId;
    private BlobId blobId;

    /**
     *
     * Creates a new import request.
     *
     * @param ownerId the folder where the new form is to be imported
     * @param blobId the blobId of the file from which the FormClass and FormInstances are to be imported
     */
    public ImportRequest(ResourceId ownerId, BlobId blobId) {
        this.ownerId = ownerId;
        this.blobId = blobId;
    }

    @Override
    public Promise<UserTask> send(RemoteStoreService service) {
        return service.startImport(ownerId, blobId);
    }
}
