package org.activityinfo.client;

import com.google.common.io.ByteSource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.tasks.UserTask;

import javax.ws.rs.core.MediaType;
import java.io.IOException;

public class Folder {

    private ActivityInfoClient client;
    private ResourceNode node;

    public Folder(ActivityInfoClient client, ResourceNode node) {

        this.client = client;
        this.node = node;
    }

    public ResourceId getId() {
        return this.node.getId();
    }

    public String getLabel() {
        return this.node.getLabel();
    }

    public UserTask importDatafile(String fileName, ByteSource byteSource) throws IOException {

        // First send the data file to the server
        BlobId blobId = BlobId.generate();
        client.postBlob(blobId, fileName, MediaType.APPLICATION_OCTET_STREAM_TYPE, byteSource);

        // Now initiate the import
        return client.startImport(node.getId(), blobId);
    }

}
