package org.activityinfo.service;

import org.activityinfo.server.util.jaxrs.AbstractRestModule;
import org.activityinfo.service.blob.BlobDownloadServlet;
import org.activityinfo.service.blob.ThumbnailDownloadServlet;
import org.activityinfo.service.writer.RecordBodyReader;
import org.activityinfo.service.writer.RecordBodyWriter;
import org.activityinfo.service.writer.ResourceBodyWriter;
import org.activityinfo.service.writer.TableDataJsonWriter;

public class ServiceModule extends AbstractRestModule {

    @Override
    protected void configureResources() {
        bindResource(ServiceResources.class);

        // Readers & Writers
        bind(RecordBodyReader.class);
        bind(ResourceBodyWriter.class);
        bind(TableDataJsonWriter.class);
        bind(RecordBodyWriter.class);

        serve("/service/blobdownload").with(BlobDownloadServlet.class);
        serve("/service/thumbnail").with(ThumbnailDownloadServlet.class);
    }
}
