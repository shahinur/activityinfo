package org.activityinfo.store.blob;

import com.google.inject.AbstractModule;
import org.activityinfo.service.blob.UserBlobService;

public class GcsUserBlobServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(UserBlobService.class).to(GcsUserBlobService.class);
    }
}
