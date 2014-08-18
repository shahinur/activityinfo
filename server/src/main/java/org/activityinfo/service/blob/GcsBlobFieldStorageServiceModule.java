package org.activityinfo.service.blob;

import com.google.inject.Provides;
import com.google.inject.servlet.ServletModule;
import org.activityinfo.service.DeploymentConfiguration;

public class GcsBlobFieldStorageServiceModule extends ServletModule {
    @Provides
    public BlobFieldStorageService provideBlobFieldStorageService(DeploymentConfiguration config) {
        return new GcsBlobFieldStorageService(config);
    }
}
