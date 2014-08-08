package org.activityinfo.service;

import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.tools.cloudstorage.*;
import com.google.gson.JsonParser;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import org.apache.poi.util.IOUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;

@Path("/service")
public class ServiceResources {

    public static final String GOOGLE_STORAGE_PREFIX = "/gs/";

    private final String defaultGcsBucketName = AppIdentityServiceFactory.getAppIdentityService().getDefaultGcsBucketName();

    private final JsonParser jsonParser = new JsonParser();

    private final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    private final GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());

    @POST
    @Path("blob/{formClassId}/{fieldId}/{blobId}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response upload(@PathParam("formClassId") String formClassId,
                    @PathParam("fieldId") String fieldId,
                    @PathParam("blobId") String blobId,
                    @FormDataParam("file") InputStream fileInputStream,
                    @FormDataParam("file") FormDataContentDisposition contentDispositionHeader) throws IOException {

        String fileName = contentDispositionHeader.getFileName();
        GcsFilename gcsFilename = new GcsFilename(getBucketName(), fileName);

        // upload is made via gcs client for "manual" uploading
        GcsOutputChannel outputChannel = gcsService.createOrReplace(gcsFilename, GcsFileOptions.getDefaultInstance());

        OutputStream outputStream = null;
        try {
            outputStream = Channels.newOutputStream(outputChannel);
            IOUtils.copy(fileInputStream, outputStream);
        } finally {
            outputStream.close();
        }



        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Path("blob/{formClassId}/{fieldId}/{blobId}")
    @Consumes("application/json")
    @Produces("application/json")
    public String downloadUrl(@PathParam("formClassId") String formClassId,
                       @PathParam("fieldId") String fieldId,
                       @PathParam("blobId") String blobId) {
        // todo
//        final String name = blobId
//
//        BlobKey blobKey = blobstoreService.createGsBlobKey(GOOGLE_STORAGE_PREFIX + fileName.getBucketName() + "/" + fileName.getObjectName());
//        blobstoreService.serve(blobKey, resp);

        return "";
    }

    @GET
    @Path("blob/thumbnail/{formClassId}/{fieldId}/{blobId}")
    @Consumes("application/json")
    @Produces("application/json")
    public String thumbnail(@PathParam("formClassId") String formClassId,
                     @PathParam("fieldId") String fieldId,
                     @PathParam("blobId") String blobId) {
        return ""; // todo
    }

    private String getBucketName() {
        // todo need bucket name here ?
        return defaultGcsBucketName;
    }
}
