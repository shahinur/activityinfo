package org.activityinfo.test;

import com.google.common.io.ByteSource;
import org.activityinfo.client.ActivityInfo;
import org.activityinfo.client.ActivityInfoClient;
import org.activityinfo.client.Folder;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.tasks.UserTask;
import org.activityinfo.service.tasks.UserTaskStatus;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.io.IOException;

import static com.google.common.io.Resources.asByteSource;
import static com.google.common.io.Resources.getResource;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class BlobTest {

    public static final MediaType XLSX_MEDIA_TYPE =
        MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private ActivityInfoClient client;

    @Before
    public void setUp() throws Exception {
        client = new ActivityInfoTestClient(TestConfig.getRootURI());
    }

    @Test
    public void upload() throws IOException {

        // Generate a new Blob id
        BlobId blobId = BlobId.generate();
        ByteSource source = asByteSource(getResource("import.xlsx"));

        // Synchronize to server
        client.postBlob(blobId, "import.xlsx", XLSX_MEDIA_TYPE, source);

        // Ensure we can download again
        ByteSource downloaded = client.getBlob(blobId);

        assertThat(downloaded.size(), equalTo(source.size()));
        assertTrue("contents downloaded equal to source", source.contentEquals(downloaded));
    }

    @Ignore
    @Test
    public void importXlsx() throws IOException {

        ActivityInfo ai = new ActivityInfo(client);

        Folder workspace = ai.createWorkspace("Import Test");
        UserTask userTask = workspace.importDatafile("import.xlsx", asByteSource(getResource("import.xlsx")));

        assertThat(userTask.getStatus(), equalTo(UserTaskStatus.RUNNING));
    }
}
