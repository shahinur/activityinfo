package org.activityinfo.test;

import com.google.common.base.Strings;
import org.junit.internal.AssumptionViolatedException;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class TestConfig {

    public static URI getRootURI() {
        String testUri = System.getProperty("test.root.uri");
        if(Strings.isNullOrEmpty(testUri)) {
            throw new AssumptionViolatedException("-Dtest.root.uri has not been set.");
        }
        return UriBuilder.fromUri(testUri).build();
    }
}
