package org.activityinfo.server.endpoint.test;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.sun.jersey.multipart.FormDataParam;
import org.activityinfo.server.database.hibernate.dao.UserDAO;
import org.activityinfo.server.database.hibernate.entity.User;
import org.activityinfo.service.DeploymentConfiguration;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;

@Path("/test")
public class TestEndpoint {
    final static private String TEST_MODE = "test.endpoint";

    final private Provider<UserDAO> userDAO;

    @Inject
    public TestEndpoint(DeploymentConfiguration deploymentConfiguration, Provider<UserDAO> userDAO) {
        if ("enabled".equals(deploymentConfiguration.getProperty(TEST_MODE))) {
            this.userDAO = userDAO;
        } else {
            this.userDAO = null;
        }
    }

    @POST
    @Path("createUser")
    @Consumes(MULTIPART_FORM_DATA)
    public Response createUser(@FormDataParam("email") String email, @FormDataParam("password") String password) {
        if (userDAO == null) return Response.status(FORBIDDEN).build();

        User user = new User();
        user.setName("Test user");
        user.setEmail(email);
        user.setLocale("POSIX");
        user.changePassword(password);
        userDAO.get().persist(user);

        return Response.status(CREATED).build();
    }
}
