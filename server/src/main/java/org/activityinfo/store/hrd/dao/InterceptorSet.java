package org.activityinfo.store.hrd.dao;

import com.google.common.collect.Lists;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.store.hrd.entity.workspace.WorkspaceEntityGroup;
import org.activityinfo.store.hrd.index.FolderIndex;
import org.activityinfo.store.hrd.index.FormMetadata;
import org.activityinfo.store.hrd.index.WorkspaceIndex;
import org.activityinfo.store.hrd.index.WorkspaceLookup;
import org.activityinfo.store.hrd.tx.ReadWriteTx;

import java.util.List;

public class InterceptorSet extends Interceptor {

    private List<Interceptor> list = Lists.newArrayList();

    public InterceptorSet() {
        list.add(new WorkspaceLookup());
        list.add(new WorkspaceIndex());
        list.add(new FormMetadata());
        list.add(new FolderIndex());
    }

    @Override
    public UpdateInterceptor createUpdateInterceptor(WorkspaceEntityGroup entityGroup, AuthenticatedUser user, ReadWriteTx transaction) {
        List<UpdateInterceptor> updateInterceptors = Lists.newArrayList();
        for(Interceptor interceptor : list) {
            UpdateInterceptor updateInterceptor = interceptor.createUpdateInterceptor(entityGroup, user, transaction);
            if(updateInterceptor != null) {
                updateInterceptors.add(updateInterceptor);
            }
        }
        return new UpdateInterceptorSet(updateInterceptors);
    }

    @Override
    void onWorkspaceCreated(AuthenticatedUser user, WorkspaceEntityGroup group) {
        super.onWorkspaceCreated(user, group);
    }
}
