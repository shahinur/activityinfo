package org.activityinfo.service.core;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.model.table.TableService;
import org.activityinfo.service.core.tables.TableDataJsonWriter;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.io.StringWriter;

@Path("/service")
public class ServiceResources {

    private final JsonParser jsonParser = new JsonParser();

    private final TableService tableService;

    @Inject
    public ServiceResources(TableService tableService) {
        this.tableService = tableService;
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

}
