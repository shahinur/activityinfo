package org.activityinfo.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.model.table.TableService;
import org.activityinfo.service.gcs.GcsUploadService;
import org.activityinfo.service.tables.TableDataJsonWriter;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.StringWriter;

@Path("/service")
public class ServiceResources {

    private final JsonParser jsonParser = new JsonParser();

    private final TableService tableService;
    private final GcsUploadService gcsUploadService;

    @Inject
    public ServiceResources(TableService tableService, GcsUploadService gcsUploadService) {
        this.tableService = tableService;
        this.gcsUploadService = gcsUploadService;
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
        String uploadUrl = gcsUploadService.createUploadUrl(formClassId, fieldId, blobId);
        return Response.status(Response.Status.OK).entity(uploadUrl).build();
    }

}
