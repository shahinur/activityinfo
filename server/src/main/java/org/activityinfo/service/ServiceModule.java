package org.activityinfo.service;

import org.activityinfo.server.util.jaxrs.AbstractRestModule;
import org.activityinfo.service.blob.BlobDownloadServlet;
import org.activityinfo.service.blob.ThumbnailDownloadServlet;

public class ServiceModule extends AbstractRestModule {

    @Override
    protected void configureResources() {
        serve("/service/blobdownload").with(BlobDownloadServlet.class);
        serve("/service/thumbnail").with(ThumbnailDownloadServlet.class);
    }
}
