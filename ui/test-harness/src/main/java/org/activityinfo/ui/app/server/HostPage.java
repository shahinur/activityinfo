package org.activityinfo.ui.app.server;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.ByteSource;
import org.activityinfo.ui.style.BaseStyleResources;
import org.activityinfo.ui.vdom.shared.Stylesheet;
import org.activityinfo.ui.vdom.shared.html.HtmlRenderer;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.Resources.*;
import static org.activityinfo.ui.style.PagePreLoader.preLoader;

@Path("/")
public class HostPage {

    private final BaseStyleResources style;

    public HostPage() throws IOException {
        style = BaseStyleResources.load();
    }

    @GET
    @Path("/index.html")
    public Response get() {

        VTree tree = TestHostPage.renderPage(new HostPageContext(style), preLoader());

        HtmlRenderer renderer = new HtmlRenderer();
        renderer.writeDocTypeDeclaration();
        tree.accept(renderer);

        return Response.ok().type(MediaType.TEXT_HTML).entity(renderer.getHtml()).build();
    }

    @GET
    @Path("/assets/{name}")
    public Response getAssets(@PathParam("name") String strongName) throws IOException {
        ByteSource source = asByteSource(style.getClasspathResourceUrl(strongName));
        MediaType type = mediaFromExtension(strongName);

        return Response.ok().type(type).entity(source.read()).build();
    }

    /**
     * Serve all of our stylesheets whole during development, they will be
     * aggregated/minified/etc for deployment
     */
    @GET
    @Path("/style/{classpath: .*}")
    @Produces("text/css")
    public String getStylesheet(@PathParam("classpath") List<PathSegment> classPathSegments) throws Exception {

        try {
            Class<?> componentClass = Class.forName(Joiner.on('.').join(classPathSegments));
            Stylesheet stylesheet = componentClass.getAnnotation(Stylesheet.class);
            if (stylesheet == null) {
                throw new IllegalStateException("Class " + componentClass + " has no @Stylesheet annotation");
            }
            return asCharSource(getResource(componentClass, stylesheet.value()), UTF_8).read();

        } catch (ClassNotFoundException e) {

            // try serving as regular resource
            return asCharSource(getResource(Joiner.on('/').join(classPathSegments)), Charsets.UTF_8).read();

        }
    }

    private MediaType mediaFromExtension(String strongName) {
        if(strongName.endsWith("css")) {
            return MediaType.valueOf("text/css");
        } else {
            return MediaType.APPLICATION_OCTET_STREAM_TYPE;
        }
    }



}
