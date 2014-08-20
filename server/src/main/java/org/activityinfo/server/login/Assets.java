package org.activityinfo.server.login;

import com.google.common.io.ByteSource;
import com.google.common.io.Resources;
import org.activityinfo.ui.style.BaseStyleResources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/assets")
public class Assets  {

    private final BaseStyleResources style;

    public Assets(BaseStyleResources style) {
        this.style = style;
    }

    @GET
    @Path("{name}")
    public javax.ws.rs.core.Response getAssets(@PathParam("name") String strongName) throws IOException {
        ByteSource source = Resources.asByteSource(style.getClasspathResourceUrl(strongName));
        MediaType type = mediaFromExtension(strongName);

        return javax.ws.rs.core.Response.ok().type(type).entity(source.read()).build();
    }

    private MediaType mediaFromExtension(String strongName) {
        if(strongName.endsWith("css")) {
            return MediaType.valueOf("text/css");
        } else {
            return MediaType.APPLICATION_OCTET_STREAM_TYPE;
        }
    }}
