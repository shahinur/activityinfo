package org.activityinfo.store.hrd.dao;

import com.google.common.collect.Lists;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.store.hrd.entity.workspace.WorkspaceEntityGroup;
import org.activityinfo.store.hrd.tx.WritableTx;

import java.util.List;

public class InterceptorCollection implements Interceptor {

    private final List<Interceptor> interceptors;

    public InterceptorCollection(Interceptor... interceptors) {
        this.interceptors = Lists.newArrayList(interceptors);
    }

    public UpdateInterceptor beginUpdate(WorkspaceEntityGroup workspace, AuthenticatedUser user, WritableTx tx) {

        final List<UpdateInterceptor> updateInterceptors = Lists.newArrayList();
        for(Interceptor interceptor : interceptors) {
            UpdateInterceptor ui = interceptor.beginUpdate(workspace, user, tx);
            if(ui != null) {
                updateInterceptors.add(ui);
            }
        }
        return new UpdateInterceptor() {

            @Override
            public void onResourceCreated(Resource resource) {
                for(UpdateInterceptor updateInterceptor : updateInterceptors) {
                    updateInterceptor.onResourceCreated(resource);
                }
            }
        };
    }
}
