package org.activityinfo;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;
import org.junit.Test;

import javax.ws.rs.core.UriBuilder;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Ensures that the server is able to startup
 */
public class StartupIT {

    @Test
    public void testStartup() {

        Client client = Client.create();
        client.addFilter(new LoggingFilter());

        WebResource root = client.resource(UriBuilder.fromUri("http://localhost").port(8181).build());

        ClientResponse clientResponse = root.get(ClientResponse.class);

        assertThat(clientResponse.getStatus(), equalTo(ClientResponse.Status.OK.getStatusCode()));
    }
}
