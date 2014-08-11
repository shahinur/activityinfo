package org.activityinfo.service.blob;

import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.PublicCertificate;

import java.util.Collection;

public class TestingIdentityService implements AppIdentityService {
    @Override
    public SigningResult signForApp(byte[] bytes) {
        return new SigningResult("foo", bytes);
    }

    @Override
    public Collection<PublicCertificate> getPublicCertificatesForApp() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getServiceAccountName() {
        return "135288259907-k64g5vuv9en1o89on1ru16hrusvimn9t@developer.gserviceaccount.com";
    }

    @Override
    public String getDefaultGcsBucketName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public GetAccessTokenResult getAccessTokenUncached(Iterable<String> strings) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GetAccessTokenResult getAccessToken(Iterable<String> strings) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ParsedAppId parseFullAppId(String s) {
        throw new UnsupportedOperationException();
    }
}
