package org.activityinfo.ui.client.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.model.table.TableService;

/**
 * Wrapper over a {@code TableService} which serializes/deserializes TableModels
 * to test the serialization mechanism
 */
public class SerializationTestingTableService implements TableService {

    private final TableService tableService;
    private final JsonParser jsonParser = new JsonParser();

    public SerializationTestingTableService(TableService tableService) {
        this.tableService = tableService;
    }

    @Override
    public TableData buildTable(TableModel tableModel) {

        String json = Resources.toJson(tableModel.asRecord());
        JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
        Record tableModelRecord = Resources.recordFromJson(jsonObject);

        tableModel = TableModel.fromRecord(tableModelRecord);

        return tableService.buildTable(tableModel);
    }
}
