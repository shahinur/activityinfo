package org.activityinfo.ui.app.server;

import com.google.common.io.ByteSource;
import com.google.common.io.Resources;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.chrome.Chrome;
import org.activityinfo.ui.style.BaseStyleResources;
import org.activityinfo.ui.vdom.shared.html.HtmlRenderer;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/")
public class HostPage {

    private final BaseStyleResources style;

    public HostPage() throws IOException {
        style = BaseStyleResources.load();
    }

    @GET
    @Path("/index.html")
    public Response get() {

        Application application = new Application(null);

        VTree tree = Chrome.renderPage(new HostPageContext(style), application);

        HtmlRenderer renderer = new HtmlRenderer();
        renderer.writeDocTypeDeclaration();
        tree.accept(renderer);

        return Response.ok().type(MediaType.TEXT_HTML).entity(renderer.getHtml()).build();
    }

    @GET
    @Path("/assets/{name}")
    public Response getAssets(@PathParam("name") String strongName) throws IOException {
        ByteSource source = Resources.asByteSource(style.getClasspathResourceUrl(strongName));
        MediaType type = mediaFromExtension(strongName);

        return Response.ok().type(type).entity(source.read()).build();
    }

    private MediaType mediaFromExtension(String strongName) {
        if(strongName.endsWith("css")) {
            return MediaType.valueOf("text/css");
        } else {
            return MediaType.APPLICATION_OCTET_STREAM_TYPE;
        }
    }
}
