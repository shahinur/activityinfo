package org.activityinfo.store.hrd.index;

import com.google.common.collect.Maps;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.store.hrd.dao.Interceptor;
import org.activityinfo.store.hrd.dao.UpdateInterceptor;
import org.activityinfo.store.hrd.entity.workspace.FormMetaEntry;
import org.activityinfo.store.hrd.entity.workspace.FormMetaEntryKey;
import org.activityinfo.store.hrd.entity.workspace.LatestVersion;
import org.activityinfo.store.hrd.entity.workspace.WorkspaceEntityGroup;
import org.activityinfo.store.hrd.tx.ReadWriteTx;

import java.util.Map;

/**
 * Index that tracks updates to a FormClass and any of instances,
 * and provides a consistent ordering of FormInstances by adding a rowIndex property to
 * the LatestVersion key
 */
public class FormMetadata extends Interceptor {

    @Override
    public UpdateInterceptor createUpdateInterceptor(WorkspaceEntityGroup entityGroup, AuthenticatedUser user, ReadWriteTx transaction) {
        return new Updater(entityGroup, transaction);
    }


    private static boolean isUserFormClass(LatestVersion resource) {
        return
            resource.getClassId().equals(FormClass.CLASS_ID) &&
           !resource.getResourceId().isApplicationDefined();
    }


    private static boolean isUserFormInstance(LatestVersion resource) {
        return
           !resource.getClassId().isApplicationDefined() &&
            resource.getOwnerId().equals(resource.getClassId());
    }

    private static class Updater extends UpdateInterceptor {

        private final WorkspaceEntityGroup workspace;
        private ReadWriteTx transaction;
        private Map<ResourceId, FormMetaEntry> formMap = Maps.newHashMap();

        private Updater(WorkspaceEntityGroup workspace, ReadWriteTx transaction) {
            this.workspace = workspace;
            this.transaction = transaction;
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

        @Override
        public void onResourceCreated(LatestVersion latestVersion) {
            if(isUserFormClass(latestVersion)) {
                onFormClassCreated(latestVersion.getResourceId());
            } else if(isUserFormInstance(latestVersion)) {
                onFormInstanceCreated(latestVersion);
            }
        }

        private void onFormClassCreated(ResourceId formClassId) {
            formMap.put(formClassId, new FormMetaEntry(workspace, formClassId));
        }

        private void onFormInstanceCreated(LatestVersion latestVersion) {
            FormMetaEntry metadata = getEntry(latestVersion.getClassId());
            long newCount = metadata.incrementInstanceCount();
            latestVersion.setRowIndex(newCount);
        }


        @Override
        public void onResourceUpdated(LatestVersion latestVersion) {

        }

        @Override
        public void flush(long updateVersion) {
            for(FormMetaEntry entry : formMap.values()) {
                entry.setCacheKey(updateVersion);
                transaction.put(entry);
            }
        }
    }
}
