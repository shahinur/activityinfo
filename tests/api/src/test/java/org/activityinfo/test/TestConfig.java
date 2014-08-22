package org.activityinfo.test;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class TestConfig {

    public static URI getRootURI() {
        return UriBuilder.fromUri(System.getProperty("test.root.uri")).build();
    }
}
