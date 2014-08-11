package org.activityinfo.service.blob;

import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.common.collect.Maps;
import com.google.common.io.BaseEncoding;

import java.util.Map;

/**
 * @see <a href="https://developers.google.com/storage/docs/reference-methods?csw=1#postobject">GCS Docs</a>
 */
public class GcsUploadCredentialBuilder {

    public static final String END_POINT = "https://storage.googleapis.com";

    public static final int BYTES_IN_MEGA_BYTE = 1024 * 1024;

    private GcsPolicyBuilder policyDocument = new GcsPolicyBuilder();
    private Map<String, String> formFields = Maps.newHashMap();
    private AppIdentityService identityService;


    public GcsUploadCredentialBuilder() {
        this.identityService = AppIdentityServiceFactory.getAppIdentityService();
    }

    public GcsUploadCredentialBuilder(AppIdentityService identityService) {
        this.identityService = identityService;
    }

    /**
     *
     * @param bucketName The name of the bucket that you want to upload to.
     */
    public GcsUploadCredentialBuilder setBucket(String bucketName) {
        policyDocument.bucketNameMustEqual(bucketName);
        formFields.put("bucket", bucketName);
        return this;
    }

    public GcsUploadCredentialBuilder setKey(String objectKey) {
        policyDocument.keyMustEqual(objectKey);
        formFields.put("key", objectKey);
        return this;
    }

    public GcsUploadCredentialBuilder setMaxContentLength(long maxBytes) {
        policyDocument.contentLengthMustBeBetween(0, maxBytes);
        return this;
    }

    public GcsUploadCredentialBuilder setMaxContentLengthInMegabytes(int megabytes) {
        return setMaxContentLength(megabytes * BYTES_IN_MEGA_BYTE);
    }

    public UploadCredentials build() {

        byte[] policy = policyDocument.toJson();
        AppIdentityService.SigningResult signature = identityService.signForApp(policy);

        formFields.put("GoogleAccessId", identityService.getServiceAccountName());
        formFields.put("policy", BaseEncoding.base64().encode(policy));
        formFields.put("signature", BaseEncoding.base64().encode(signature.getSignature()));

        return new UploadCredentials(END_POINT, "POST", formFields);
    }
}
