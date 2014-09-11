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
import org.activityinfo.server.authentication.ServerSideAuthProvider;
import org.activityinfo.server.login.model.RootPageModel;
import org.activityinfo.server.util.logging.LogException;
import org.activityinfo.service.DeploymentConfiguration;
import org.activityinfo.ui.app.client.chrome.Chrome;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.style.BaseStyleResources;
import org.activityinfo.ui.vdom.shared.html.HtmlRenderer;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.*;
import java.util.HashMap;

import static org.activityinfo.ui.style.PagePreLoader.preLoader;

@Path(HostController.ENDPOINT)
public class HostController {
    public static final String ENDPOINT = "/";

    private final DeploymentConfiguration deployConfig;
    private final ServerSideAuthProvider authProvider;
    private final BaseStyleResources style;

    @Inject
    public HostController(DeploymentConfiguration deployConfig,
                          ServerSideAuthProvider authProvider,
                          BaseStyleResources style) {
        super();
        this.deployConfig = deployConfig;
        this.authProvider = authProvider;
        this.style = style;
    }

    @GET @Produces(MediaType.TEXT_HTML) @LogException(emailAlert = true)
    public Response getHostPage(@Context UriInfo uri,
                                @Context HttpServletRequest req,
                                @QueryParam("redirect") boolean redirect) throws Exception {

        if (!authProvider.isAuthenticated()) {
            // Otherwise, go to the default ActivityInfo root page
            return Response.ok(new RootPageModel().asViewable())
                           .type(MediaType.TEXT_HTML)
                           .cacheControl(CacheControl.valueOf("no-cache"))
                           .build();
        }

        if (redirect) {
            return Response.seeOther(uri.getAbsolutePathBuilder().replacePath(ENDPOINT).build()).build();
        }


        Application application = new Application(null);

        VTree tree = Chrome.renderPage(new HostPageContext(style), preLoader());

        HtmlRenderer renderer = new HtmlRenderer();
        renderer.writeDocTypeDeclaration();
        tree.accept(renderer);

        return Response.ok().type(MediaType.TEXT_HTML)
                .entity(renderer.getHtml())
                .cacheControl(CacheControl.valueOf("no-cache"))
                .build();

    }

    /**
     * @return a simple error page indicating that the GWT app does not support
     * the user's browser. This is necessary because user-agent based selection
     * is done server-side when the javascript is requested, so all we can do
     * is redirect the user to this page.
     */
    @GET @Path("/unsupportedBrowser")
    public Viewable getUnsupportedBrowserMessage() {
        return new Viewable("/page/UnsupportedBrowser.ftl", new HashMap());
    }

}
