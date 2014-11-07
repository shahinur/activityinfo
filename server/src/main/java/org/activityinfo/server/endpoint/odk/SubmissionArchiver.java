package org.activityinfo.server.endpoint.odk;

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
import java.util.logging.Level;
import java.util.logging.Logger;

public class SubmissionArchiver {

    public static final String BACKUPSERVICE_GCS_BUCKET_NAME = "odk.backup.gcs.bucket.name";

    private static final Logger LOGGER = Logger.getLogger(SubmissionArchiver.class.getName());

    private final String bucketName;

    @Inject
    public SubmissionArchiver(DeploymentConfiguration config) {
        this.bucketName = config.getProperty(SubmissionArchiver.BACKUPSERVICE_GCS_BUCKET_NAME);
    }

    public void backup(ResourceId resourceId, ByteSource byteSource) throws IOException {
        GcsFilename gcsFilename = new GcsFilename(bucketName, resourceId.asString());
        GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());

        GcsFileOptions gcsFileOptions = new Builder().mimeType(MediaType.MULTIPART_FORM_DATA).build();
        GcsOutputChannel channel = gcsService.createOrReplace(gcsFilename, gcsFileOptions);

        try (OutputStream outputStream = Channels.newOutputStream(channel)) {
            byteSource.copyTo(outputStream);
        }

        LOGGER.log(Level.INFO, "Archived XForm for id " + resourceId + " to " + gcsFilename);
    }
}
