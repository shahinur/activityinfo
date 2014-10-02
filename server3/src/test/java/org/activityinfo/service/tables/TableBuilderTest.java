package org.activityinfo.service.tables;

import org.activityinfo.model.formTree.FieldPath;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.store.test.TestResourceStore;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.activityinfo.model.system.ApplicationProperties.LABEL_PROPERTY;
import static org.activityinfo.model.system.ApplicationProperties.PARENT_PROPERTY;

public class TableBuilderTest {

    public static final ResourceId PROVINCE = CuidAdapter.adminLevelFormClass(1);
    public static final ResourceId DISTRICT = CuidAdapter.adminLevelFormClass(2);
    public static final ResourceId TERRITOIRE = CuidAdapter.adminLevelFormClass(3);
    public static final ResourceId SECTEUR = CuidAdapter.adminLevelFormClass(4);
    public static final ResourceId GROUPEMENT = CuidAdapter.adminLevelFormClass(5);


    private TestResourceStore store;
    private TableBuilder tableService;

    @Before
    public void setup() throws IOException {
        store = new TestResourceStore().load("nfi-import.json");
        tableService = new TableBuilder(store);
    }

    @Test
    public void singleJoin() throws Exception {
        TableModel tableModel = new TableModel(DISTRICT);
        tableModel.selectResourceId().as("id");
        tableModel.selectField(LABEL_PROPERTY).as("district");
        tableModel.selectField(new FieldPath(PARENT_PROPERTY, LABEL_PROPERTY)).as("province");

        TableData tableData = tableService.buildTable(tableModel);

        System.out.println(tableData);
    }

    @Test
    public void twoLinkJoin() throws Exception {
        TableModel tableModel = new TableModel(TERRITOIRE);
        tableModel.selectResourceId().as("id");
        tableModel.selectField(LABEL_PROPERTY).as("territoire");
        tableModel.selectField(new FieldPath(PARENT_PROPERTY, LABEL_PROPERTY)).as("district");
        tableModel.selectField(new FieldPath(PARENT_PROPERTY, PARENT_PROPERTY, LABEL_PROPERTY)).as("province");

        TableData tableData = tableService.buildTable(tableModel);

        System.out.println(tableData);
    }

}