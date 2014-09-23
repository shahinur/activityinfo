package org.activityinfo.test;

import com.sun.jersey.api.client.UniformInterfaceException;
import org.activityinfo.client.ActivityInfoClient;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.junit.Test;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AuthorizationTest {
    final private static ResourceId resourceId = Resources.generateId();

    @Test
    public void test() {
        ActivityInfoClient client = new ActivityInfoClient(TestConfig.getRootURI(), "auth.test@mailinator.com", "auth");

        try {
            client.get(resourceId);
            fail("An ID locally generated at random should not already be present inside the server's resource store!");
        } catch (UniformInterfaceException uniformInterfaceException) {
            assertEquals(NOT_FOUND.getStatusCode(), uniformInterfaceException.getResponse().getStatus());
        }
    }
}

