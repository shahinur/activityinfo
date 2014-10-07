package org.activityinfo.store.hrd.index;

import com.google.common.base.Strings;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.system.ApplicationProperties;
import org.activityinfo.store.BadRequestException;
import org.activityinfo.store.hrd.dao.Interceptor;
import org.activityinfo.store.hrd.dao.UpdateInterceptor;
import org.activityinfo.store.hrd.entity.workspace.LatestVersion;
import org.activityinfo.store.hrd.entity.workspace.WorkspaceEntityGroup;
import org.activityinfo.store.hrd.tx.ReadWriteTx;

public class FolderIndex extends Interceptor {

    @Override
    public UpdateInterceptor createUpdateInterceptor(WorkspaceEntityGroup entityGroup, AuthenticatedUser user, ReadWriteTx transaction) {
        return new FolderIndexUpdater();
    }

    public static String getLabelAndAssertNonEmpty(Record record) {
        // For the most part we want to be pretty generous about what we'll accept
        // from users because it's better to get their data safely in and mark it as invalid
        // and strand them in the middle of some refugee camp fighting with a form submission,
        // but for some basic classes like folders and forms, we need to enforce basic rules
        ResourceId classId = record.getClassId();
        String labelFieldName = ApplicationProperties.getLabelPropertyName(classId);
        String label = record.isString(labelFieldName);
        if (Strings.isNullOrEmpty(label)) {
            throw new BadRequestException(String.format("Resources of class %s must have a label property " +
                " with id %s", classId, labelFieldName));
        }
        return label;
    }

    public static boolean isFolderItem(Resource resource) {
        throw new UnsupportedOperationException();
    }

    public class FolderIndexUpdater extends UpdateInterceptor {

        public void maybeUpdateLabelProperty(LatestVersion latestVersion) {
            if(ApplicationProperties.isFolderItem(latestVersion.getClassId())) {
                latestVersion.setLabel(getLabelAndAssertNonEmpty(latestVersion.getRecord()));
            }
        }

        @Override
        public void onResourceCreated(LatestVersion latestVersion) {
            maybeUpdateLabelProperty(latestVersion);
        }

        @Override
        public void onResourceUpdated(LatestVersion latestVersion) {
            maybeUpdateLabelProperty(latestVersion);
        }
    }
}
