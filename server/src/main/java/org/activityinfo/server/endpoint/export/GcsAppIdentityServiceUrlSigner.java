package org.activityinfo.server.endpoint.export;

import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.common.io.BaseEncoding;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;

public class GcsAppIdentityServiceUrlSigner  {

    private static final int EXPIRATION_TIME = 5;
    private static final String BASE_URL = "https://storage.googleapis.com";

    private final AppIdentityService identityService = AppIdentityServiceFactory.getAppIdentityService();

    public String getSignedUrl(final String httpVerb, final String path) throws Exception {
        final long expiration = expiration();
        final String unsigned = stringToSign(expiration, path, httpVerb);
        final String signature = sign(unsigned);

        return new StringBuilder(BASE_URL)
        .append("/")
        .append(path)
        .append("?GoogleAccessId=")
        .append(clientId())
        .append("&Expires=")
        .append(expiration)
        .append("&Signature=")
        .append(URLEncoder.encode(signature, "UTF-8")).toString();
    }

    private static long expiration() {
        final long unitMil = 1000l;
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, EXPIRATION_TIME);
        return calendar.getTimeInMillis() / unitMil;
    }

    private String stringToSign(final long expiration, String path, String httpVerb) {
        final String contentType = "";
        final String contentMD5 = "";
        final String canonicalizedExtensionHeaders = "";
        final String canonicalizedResource = "/" + path;
        return httpVerb + "\n" + contentMD5 + "\n" + contentType + "\n"
                        + expiration + "\n" + canonicalizedExtensionHeaders + canonicalizedResource;
    }

    protected String sign(final String stringToSign) throws UnsupportedEncodingException {
        final AppIdentityService.SigningResult signingResult = identityService
                .signForApp(stringToSign.getBytes());

        return BaseEncoding.base64().encode(signingResult.getSignature());
    }

    protected String clientId() {
        return identityService.getServiceAccountName();
    }
}