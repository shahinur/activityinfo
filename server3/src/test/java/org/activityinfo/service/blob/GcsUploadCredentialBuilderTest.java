package org.activityinfo.service.blob;

import com.google.appengine.api.appidentity.AppIdentityService;
import org.junit.Test;

import static org.junit.Assert.*;

public class GcsUploadCredentialBuilderTest {

    @Test
    public void test() {

        GcsUploadCredentialBuilder credentials = new GcsUploadCredentialBuilder(new TestingIdentityService());
        credentials.setBucket("activityinfo-field-blobs");
        credentials.setKey(BlobId.generate().asString());
        credentials.setMaxContentLengthInMegabytes(10);

        System.out.println(credentials.build());

    }
}