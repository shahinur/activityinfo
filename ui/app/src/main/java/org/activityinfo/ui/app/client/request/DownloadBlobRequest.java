package org.activityinfo.ui.app.client.request;

import com.google.gwt.user.client.Window;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.store.RemoteStoreService;

public class DownloadBlobRequest implements Request {

    private BlobId blobId;

    public DownloadBlobRequest(BlobId blobId) {
        this.blobId = blobId;
    }

    @Override
    public Promise send(RemoteStoreService service) {
        Window.Location.assign(service.getBlobDownloadUrl(blobId));
        return new Promise<Void>();
    }
}
