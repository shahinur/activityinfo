package org.activityinfo.store.hrd.entity.workspace;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import org.activityinfo.model.resource.ResourceId;

public class FormMetaEntryKey implements WorkspaceEntityGroupKey<FormMetaEntry> {

    public static final String KIND = "F";

    private final Key key;

    public FormMetaEntryKey(WorkspaceEntityGroup entityGroup, ResourceId formClassId) {
        this.key = KeyFactory.createKey(entityGroup.getRootKey(), KIND, formClassId.asString());
    }

    public FormMetaEntryKey(LatestVersionKey key) {
        this(key.getWorkspace(), key.getResourceId());
    }

    @Override
    public Key unwrap() {
        return key;
    }

    @Override
    public FormMetaEntry wrapEntity(Entity entity) {
        return new FormMetaEntry(this, entity);
    }

    @Override
    public WorkspaceEntityGroup getWorkspace() {
        return new WorkspaceEntityGroup(key.getParent());
    }

    public ResourceId getFormClassId() {
        return ResourceId.valueOf(key.getName());
    }

    @Override
    public String toString() {
        return getWorkspace() + "/FormMetadata[" + getFormClassId() + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FormMetaEntryKey that = (FormMetaEntryKey) o;

        if (!key.equals(that.key)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

}
