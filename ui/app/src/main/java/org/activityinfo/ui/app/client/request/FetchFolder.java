package org.activityinfo.ui.app.client.request;

import org.activityinfo.model.resource.FolderProjection;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.store.RemoteStoreService;

import javax.annotation.Nonnull;

public class FetchFolder implements Request<FolderProjection> {

    @Nonnull
    private final ResourceId folderId;

    public FetchFolder(@Nonnull ResourceId folderId) {
        this.folderId = folderId;
    }


    @Nonnull
    public ResourceId getFolderId() {
        return folderId;
    }

    @Override
    public Promise<FolderProjection> send(RemoteStoreService service) {
        return service.getFolder(folderId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FetchFolder that = (FetchFolder) o;

        if (!folderId.equals(that.folderId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return folderId.hashCode();
    }
}
