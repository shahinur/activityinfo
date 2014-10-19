package org.activityinfo.server.login;

/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.google.inject.Inject;
import com.sun.jersey.api.view.Viewable;
import org.activityinfo.server.DeploymentEnvironment;
import org.activityinfo.server.authentication.Authenticator;
import org.activityinfo.server.database.hibernate.dao.Transactional;
import org.activityinfo.server.database.hibernate.dao.UserDAO;
import org.activityinfo.server.database.hibernate.entity.User;
import org.activityinfo.server.login.exception.LoginException;
import org.activityinfo.server.login.model.LoginPageModel;
import org.activityinfo.server.util.logging.LogException;
import org.activityinfo.service.DeploymentConfiguration;

import javax.inject.Provider;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;


@Path(LoginController.ENDPOINT)
public class LoginController {

    public static final String ENDPOINT = "/login";

    private static final String TEST_MODE = "enable.test.user.creation";

    @Inject
    private DeploymentConfiguration deploymentConfiguration;

    @Inject
    private Provider<Authenticator> authenticator;

    @Inject
    private Provider<AuthTokenProvider> authTokenProvider;

    @Inject
    private Provider<UserDAO> userDAO;

    @GET @LogException(emailAlert = true) @Produces(MediaType.TEXT_HTML)
    public Viewable getLoginPage(@Context UriInfo uri) throws Exception {
        LoginPageModel model = new LoginPageModel();
        return model.asViewable();
    }

    @POST @Path("ajax")
    public Response ajaxLogin(@FormParam("email") String email,
                              @FormParam("password") String password) throws Exception {

        User user = userDAO.get().findUserByEmail(email);
        checkPassword(password, user);

        return Response.ok().cookie(authTokenProvider.get().createNewAuthCookies(user)).build();
    }


    @POST
    @Path("createUser")
    @Consumes(APPLICATION_FORM_URLENCODED)
    @Transactional
    public Response createUser(@FormParam("email") String email, @FormParam("password") String password) {

        if (DeploymentEnvironment.isAppEngineProduction() &&
              !"enabled".equals(deploymentConfiguration.getProperty(TEST_MODE))) {
            throw new WebApplicationException(FORBIDDEN);
        }

        User user = new User();
        user.setName("Test user");
        user.setEmail(email);
        user.setLocale("POSIX");
        user.changePassword(password);
        userDAO.get().persist(user);

        return Response.status(CREATED).build();
    }

    @POST
    public Response login(@Context UriInfo uri,
                          @FormParam("email") String email,
                          @FormParam("password") String password) throws Exception {

        User user;
        try {
            user = userDAO.get().findUserByEmail(email);
            checkPassword(password, user);
        } catch (Exception e) {
            LoginPageModel model = LoginPageModel.unsuccessful();

            return Response.ok(model).type(MediaType.TEXT_HTML).build();
        }

        return Response.seeOther(uri.getAbsolutePathBuilder().replacePath("/").build())
                       .cookie(authTokenProvider.get().createNewAuthCookies(user))
                       .build();
    }

    private void checkPassword(String password, User user) throws LoginException {

        if (!authenticator.get().check(user, password)) {
            throw new LoginException();
        }
    }
}
