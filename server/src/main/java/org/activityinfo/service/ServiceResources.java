package org.activityinfo.service;

import com.google.api.client.googleapis.extensions.appengine.auth.oauth2.AppIdentityCredential;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import org.activityinfo.legacy.shared.auth.AuthenticatedUser;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.model.table.TableService;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.blob.GcsBlobFieldStorageService;
import org.activityinfo.service.blob.GcsUploadCredentialBuilder;
import org.activityinfo.service.blob.UploadCredentials;
import org.activityinfo.service.tables.TableDataJsonWriter;

import javax.inject.Provider;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.StringWriter;

@Path("/service")
public class ServiceResources {

    public static final String GOOGLE_STORAGE_PREFIX = "/gs/";

    private final JsonParser jsonParser = new JsonParser();

    private final TableService tableService;
    private final GcsBlobFieldStorageService blobFieldStorageService;
    private final Provider<AuthenticatedUser> authProvider;

    @Inject
    public ServiceResources(TableService tableService,
                            GcsBlobFieldStorageService blobFieldStorageService,
                            Provider<AuthenticatedUser> authProvider) {
        this.tableService = tableService;
        this.blobFieldStorageService = blobFieldStorageService;
        this.authProvider = authProvider;
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

//    @POST
//    @Path("blob/{blobId}")
//    @Produces("application/json")
//    public UploadCredentials createUploadUrl(@PathParam("blobId") String blobId) throws IOException {
//
//        return blobFieldStorageService.getUploadCredentials(authProvider.get().getUserResourceId(),
//                new BlobId(blobId));
//    }

}
