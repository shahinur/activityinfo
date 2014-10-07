package org.activityinfo.store.hrd.dao;

import org.activityinfo.store.hrd.entity.workspace.LatestVersion;

import java.util.List;

class UpdateInterceptorSet extends UpdateInterceptor {
    private List<UpdateInterceptor> updaters;

    public UpdateInterceptorSet(List<UpdateInterceptor> updaters) {
        this.updaters = updaters;
    }

    @Override
    public void onResourceCreated(LatestVersion latestVersion) {
        for(UpdateInterceptor updater : updaters) {
            updater.onResourceCreated(latestVersion);
        }
    }

    public void onResourceUpdated(LatestVersion latestVersion) {
        for(UpdateInterceptor updater : updaters) {
            updater.onResourceCreated(latestVersion);
        }
    }

    @Override
    public void flush(long updateVersion) {
        for(UpdateInterceptor updater :  updaters) {
            updater.flush(updateVersion);
        }
    }
}
