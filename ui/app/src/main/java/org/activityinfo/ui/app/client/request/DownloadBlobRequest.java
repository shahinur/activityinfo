package org.activityinfo.ui.app.client.request;

import com.google.gwt.user.client.Window;
import org.activityinfo.client.ActivityInfoAsyncClient;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.blob.BlobId;

public class DownloadBlobRequest implements Request {

    private BlobId blobId;

    public DownloadBlobRequest(BlobId blobId) {
        this.blobId = blobId;
    }

    @Override
    public Promise send(ActivityInfoAsyncClient service) {
        Window.Location.assign(service.getBlobDownloadUrl(blobId));
        return new Promise<Void>();
    }
}
