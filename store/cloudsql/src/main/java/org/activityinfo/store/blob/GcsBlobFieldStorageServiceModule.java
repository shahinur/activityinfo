package org.activityinfo.store.blob;

import com.google.inject.AbstractModule;
import org.activityinfo.service.blob.BlobFieldStorageService;

public class GcsBlobFieldStorageServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(BlobFieldStorageService.class).to(GcsBlobFieldStorageService.class);
    }
}
