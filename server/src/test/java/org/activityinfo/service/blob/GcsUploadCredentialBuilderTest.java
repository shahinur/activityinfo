package org.activityinfo.service.blob;

import org.activityinfo.server.util.blob.DevAppIdentityService;
import org.activityinfo.service.DeploymentConfiguration;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Properties;

public class GcsUploadCredentialBuilderTest {

    @Test
    public void test() {

        GcsUploadCredentialBuilder credentials = new GcsUploadCredentialBuilder(new TestingIdentityService());
        credentials.setBucket("activityinfo-field-blobs");
        credentials.setKey(BlobId.generate().asString());
        credentials.setMaxContentLengthInMegabytes(10);

        System.out.println(credentials.build());
    }

    @Test
    @Ignore("Requires key store")
    public void testDevCred() {

        Properties properties = new Properties();
        properties.put("service.account.name", "135288259907-k64g5vuv9en1o89on1ru16hrusvimn9t@developer.gserviceaccount.com");
        properties.put("service.account.p12.key.path", "/home/alex/.ssh/bdd-dev-0bcc29c72426.p12");
        properties.put("service.account.p12.key.password", "notasecret");

        DeploymentConfiguration config = new DeploymentConfiguration(properties);

        DevAppIdentityService identityService = new DevAppIdentityService(config);
        GcsUploadCredentialBuilder credentials = new GcsUploadCredentialBuilder(identityService);
        credentials.setBucket("activityinfo-field-blobs");
        credentials.setKey(BlobId.generate().asString());
        credentials.setMaxContentLengthInMegabytes(10);

        System.out.println(credentials.build());



    }
}