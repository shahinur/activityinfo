package org.activityinfo.service;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.UploadOptions;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.model.table.TableService;
import org.activityinfo.server.DeploymentEnvironment;
import org.activityinfo.server.util.config.DeploymentConfiguration;
import org.activityinfo.service.tables.TableDataJsonWriter;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.StringWriter;

@Path("/service")
public class ServiceResources {

    public static final String GOOGLE_STORAGE_PREFIX = "/gs/";

    private final JsonParser jsonParser = new JsonParser();

    private final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    private final GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());

    private final TableService tableService;
    private final DeploymentConfiguration config;

    @Inject
    public ServiceResources(TableService tableService, DeploymentConfiguration config) {
        this.tableService = tableService;
        this.config = config;
    }

    @POST
    @Path("table")
    @Consumes("application/json")
    @Produces("application/json")
    public String query(String encodedTableModel) throws IOException {

        JsonObject tableModelObject = jsonParser.parse(encodedTableModel).getAsJsonObject();
        Record tableModelRecord = Resources.recordFromJson(tableModelObject);
        TableModel tableModel = TableModel.fromRecord(tableModelRecord);

        TableData tableData = tableService.buildTable(tableModel);

        StringWriter writer = new StringWriter();
        TableDataJsonWriter jsonWriter = new TableDataJsonWriter(writer);
        jsonWriter.write(tableData);

        return writer.toString();
    }

    @POST
    @Path("blob/{formClassId}/{fieldId}/{blobId}")
    public Response createUploadUrl(@PathParam("formClassId") String formClassId,
                    @PathParam("fieldId") String fieldId,
                    @PathParam("blobId") String blobId) throws IOException {

        if (DeploymentEnvironment.isAppEngineDevelopment()) {
            // in case of direct REST call we need to authenticate request (probably via http header)
        }

        UploadOptions uploadOptions = UploadOptions.Builder.
                withGoogleStorageBucketName(config.getBlobServiceBucketName()); // force upload to GCS
        String uploadUrl = blobstoreService.createUploadUrl("/", uploadOptions); // no success handler

        return Response.status(Response.Status.OK).entity(uploadUrl).build();
    }

}
