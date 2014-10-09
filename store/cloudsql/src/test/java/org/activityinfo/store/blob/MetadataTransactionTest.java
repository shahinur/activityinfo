package org.activityinfo.store.blob;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.base.Optional;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.blob.BlobMetadata;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class MetadataTransactionTest {


    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
                    .setApplyAllHighRepJobPolicy());

    @Before
    public void setUp() throws Exception {
        helper.setUp();
    }

    @After
    public void tearDown() throws Exception {
        helper.tearDown();
    }

    @Test
    public void test() {
        BlobMetadata metadata = BlobMetadata.attachment(BlobId.generate(), "test.txt", MediaType.TEXT_PLAIN_TYPE);
        AuthenticatedUser user = new AuthenticatedUser();
        UserBlob userBlob = UserBlob.newBlob(user, metadata);

        try(MetadataTransaction tx = new MetadataTransaction()) {
            tx.put(userBlob);
            tx.commit();
        }

        try(MetadataTransaction tx = new MetadataTransaction()) {
            Optional<UserBlob> foo = tx.get(BlobId.valueOf("foo"));
            assertFalse(foo.isPresent());
        }

        try(MetadataTransaction tx = new MetadataTransaction()) {
            UserBlob reread = tx.get(metadata.getBlobId()).get();
            assertThat(reread.getBlobId(), equalTo(metadata.getBlobId()));
            assertThat(reread.getUserId(), equalTo(user.getId()));
            assertThat(reread.getMetadata().getContentDisposition(), hasProperty("fileName", equalTo("test.txt")));
            assertThat(reread.getMetadata().getContentType(), equalTo(MediaType.TEXT_PLAIN_TYPE));
        }

    }

}