package org.activityinfo.test;

import com.google.common.io.ByteSource;
import org.activityinfo.client.ActivityInfoClient;
import org.activityinfo.service.blob.BlobId;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.io.IOException;

import static com.google.common.io.Resources.asByteSource;
import static com.google.common.io.Resources.getResource;

public class BlobTest {

    public static final MediaType XLSX_MEDIA_TYPE = MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");


    @Test
    public void test() throws IOException {

        ActivityInfoClient client = new ActivityInfoClient(TestConfig.getRootURI(),
            "odk.test@mailinator.com",
            "odk.test");


        // Generate a new Blob id
        BlobId blobId = BlobId.generate();
        ByteSource source = asByteSource(getResource("import.xlsx"));

        // Synchronize to server
        client.postBlob(blobId, "import.xlsx", XLSX_MEDIA_TYPE, source);


        // Ensure we can download again
        client.getBlob(blobId);


    }
}
