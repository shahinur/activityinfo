package org.activityinfo.store.hrd.index;

import com.google.common.collect.Maps;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.store.hrd.entity.workspace.FormMetaEntry;
import org.activityinfo.store.hrd.entity.workspace.FormMetaEntryKey;
import org.activityinfo.store.hrd.entity.workspace.LatestVersion;
import org.activityinfo.store.hrd.entity.workspace.WorkspaceEntityGroup;
import org.activityinfo.store.hrd.tx.WritableTx;

import java.util.Map;

/**
 * Index that tracks updates to a FormClass and any of instances,
 * and provides a consistent ordering of FormInstances by adding a rowIndex property to
 * the LatestVersion key
 */
public class FormInstanceIndexer {

    private final WorkspaceEntityGroup workspace;
    private WritableTx transaction;

    private Map<ResourceId, FormMetaEntry> formMap = Maps.newHashMap();

    public FormInstanceIndexer(WorkspaceEntityGroup workspace, WritableTx tx) {
        this.workspace = workspace;
        this.transaction = tx;
    }

    private static boolean isUserFormClass(ResourceId id, ResourceId classId) {
        return
            classId.equals(FormClass.CLASS_ID) &&
           !id.isApplicationDefined();
    }

    private static boolean isUserFormInstance(Resource resource) {
        return
           !resource.getClassId().isApplicationDefined() &&
            resource.getOwnerId().equals(resource.getClassId());
    }

    private FormMetaEntry getEntry(ResourceId formClassId) {
        FormMetaEntry entry = formMap.get(formClassId);
        if(entry == null) {
            // The following will through an IllegalStateException if the entry can't be retrieved,
            // because it does in fact mean that there is a big problem with the state of the datastore
            // if an entry wasn't created when this form was created.
            entry = transaction.getOrThrow(new FormMetaEntryKey(workspace, formClassId));
            formMap.put(formClassId, entry);
        }
        return entry;
    }

    /**
     *
     * @return the next sequential instance index if this is a user form instance, null otherwise
     */
    public Long nextInstanceIndex(Resource resource) {
        if(isUserFormInstance(resource)) {
            FormMetaEntry metadata = getEntry(resource.getClassId());
            return metadata.incrementInstanceCount();
        } else {
            return null;
        }
    }

    public void onResourceCreated(Resource resource) {
        if(isUserFormClass(resource.getId(), resource.getValue().getClassId())) {
            onFormClassCreated(resource.getId());
        }
    }

    public void onResourceUpdated(Resource resource) {
        if(isUserFormClass(resource.getId(), resource.getClassId())) {
            onFormUpdated(resource.getId());
        } else if(isUserFormInstance(resource)) {
            onFormUpdated(resource.getClassId());
        }
    }


    public void onResourceDeleted(LatestVersion previousVersion) {
        if(isUserFormInstance(previousVersion.toResource())) {
            onFormUpdated(previousVersion.getClassId());
        }
    }

    private void onFormClassCreated(ResourceId formClassId) {
        formMap.put(formClassId, new FormMetaEntry(workspace, formClassId));
    }

    private void onFormUpdated(ResourceId formClassId) {
        FormMetaEntry entry = getEntry(formClassId);
    }

    public void flushWrites(long updateVersion) {
        for(FormMetaEntry entry : formMap.values()) {
            entry.setCacheKey(updateVersion);
            transaction.put(entry);
        }
    }

}
