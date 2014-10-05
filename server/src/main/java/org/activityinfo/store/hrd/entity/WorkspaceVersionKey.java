package org.activityinfo.store.hrd.entity;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import org.activityinfo.model.resource.ResourceId;

import javax.annotation.Nonnull;

public class WorkspaceVersionKey implements IsKey<WorkspaceVersion> {
    public static final String KIND = "WV";
    public static final String KEY_NAME = "current";

    private final WorkspaceKey workspaceKey;

    public WorkspaceVersionKey(@Nonnull WorkspaceKey workspaceKey) {
        this.workspaceKey = workspaceKey;
    }

    public WorkspaceKey getWorkspaceKey() {
        return workspaceKey;
    }

    public ResourceId getWorkspaceId() {
        return workspaceKey.getWorkspaceId();
    }

    @Override
    public Key create() {
        return KeyFactory.createKey(workspaceKey.create(), KIND, KEY_NAME);
    }
}
