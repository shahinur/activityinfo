package org.activityinfo.store.hrd.load;

import com.google.common.io.ByteSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class UrlByteSource extends ByteSource {

    private URL url;

    public UrlByteSource(String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("malformed url: " + url);
        }
    }

    @Override
    public InputStream openStream() throws IOException {
        return url.openStream();
    }
}
