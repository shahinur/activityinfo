package org.activityinfo.service;

import org.activityinfo.model.resource.ResourceStore;
import org.activityinfo.model.table.TableService;
import org.activityinfo.server.util.jaxrs.AbstractRestModule;
import org.activityinfo.service.blob.BlobDownloadServlet;
import org.activityinfo.service.blob.ThumbnailDownloadServlet;
import org.activityinfo.service.store.MySqlResourceStore;
import org.activityinfo.service.tables.TableServiceImpl;

public class ServiceModule extends AbstractRestModule {

    @Override
    protected void configureResources() {
        bindResource(ServiceResources.class);
        bind(ResourceStore.class).to(MySqlResourceStore.class);
        bind(TableService.class).to(TableServiceImpl.class);

        serve("/service/blobdownload").with(BlobDownloadServlet.class);
        serve("/service/thumbnail").with(ThumbnailDownloadServlet.class);
    }
}
