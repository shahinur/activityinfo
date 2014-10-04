package org.activityinfo.test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.representation.Form;
import org.activityinfo.client.ActivityInfoClient;
import org.junit.internal.AssumptionViolatedException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import java.net.URI;
import java.security.SecureRandom;
import java.util.Collections;

import static java.lang.Long.toHexString;
import static javax.ws.rs.core.Response.Status.*;

public class ActivityInfoTestClient extends ActivityInfoClient {
    final private static String TEST_ADDRESS_SUFFIX = "@example.com";
    final private static SecureRandom secureRandom = new SecureRandom();

    final private String accountEmail;
    final private String password;

    private ActivityInfoTestClient(URI rootUri, String accountEmail, String password) {
        super(rootUri, accountEmail, password);
        this.accountEmail = accountEmail;
        this.password = password;
    }

    public ActivityInfoTestClient(URI rootUri) {
        this(rootUri, toHexString(secureRandom.nextLong()) + TEST_ADDRESS_SUFFIX, toHexString(secureRandom.nextLong()));
        final Form form = new Form();

        form.put("email", Collections.singletonList(accountEmail));
        form.put("password", Collections.singletonList(password));

        // Creating a test user requires a special endpoint to be enabled
        Status status = fromStatusCode(
                root.path("test").path("createUser")
                .type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                .post(ClientResponse.class, form)
                .getStatus());

        if (FORBIDDEN.equals(status)) {
            throw new AssumptionViolatedException("Server is not configured to run in test mode");
        } else if (!CREATED.equals(status)) {
            throw new WebApplicationException(status);
        }
    }
}
