package org.activityinfo.service.blob;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFileOptions.Builder;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.google.common.io.ByteSource;
import com.google.inject.Inject;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.DeploymentConfiguration;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;

public class OdkFormSubmissionBackupService {
    private final String bucketName;

    @Inject
    public OdkFormSubmissionBackupService(DeploymentConfiguration config) {
        this.bucketName = config.getBackupServiceBucketName();
    }

    public void backup(ResourceId resourceId, ByteSource byteSource) throws IOException {
        GcsFilename gcsFilename = new GcsFilename(bucketName, resourceId.asString());
        GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());

        GcsFileOptions gcsFileOptions = new Builder().mimeType(MediaType.MULTIPART_FORM_DATA).build();
        GcsOutputChannel channel = gcsService.createOrReplace(gcsFilename, gcsFileOptions);

        try (OutputStream outputStream = Channels.newOutputStream(channel)) {
            byteSource.copyTo(outputStream);
        }
    }
}
