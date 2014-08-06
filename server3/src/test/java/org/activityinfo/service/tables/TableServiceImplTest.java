package org.activityinfo.service.tables;

import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.ui.client.service.TestResourceStore;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.activityinfo.model.system.ApplicationProperties.LABEL_PROPERTY;
import static org.activityinfo.model.system.ApplicationProperties.PARENT_PROPERTY;

public class TableServiceImplTest {

    public static final ResourceId PROVINCE = CuidAdapter.adminLevelFormClass(1);
    public static final ResourceId DISTRICT = CuidAdapter.adminLevelFormClass(2);
    public static final ResourceId TERRITOIRE = CuidAdapter.adminLevelFormClass(3);
    public static final ResourceId SECTEUR = CuidAdapter.adminLevelFormClass(4);
    public static final ResourceId GROUPEMENT = CuidAdapter.adminLevelFormClass(5);


    private TestResourceStore store;
    private TableServiceImpl tableService;

    @Before
    public void setup() throws IOException {
        store = new TestResourceStore().load("/dbunit/nfi-import.json");
        tableService = new TableServiceImpl(store);
    }

    @Test
    public void singleJoin() throws Exception {
        TableModel tableModel = new TableModel(DISTRICT);
        tableModel.addColumn("id").selectId();
        tableModel.addColumn("district").select().fieldPath(LABEL_PROPERTY);
        tableModel.addColumn("province").select().fieldPath(PARENT_PROPERTY, LABEL_PROPERTY);

        TableData tableData = tableService.buildTable(tableModel);

        System.out.println(tableData);
    }

    @Test
    public void twoLinkJoin() {
        TableModel tableModel = new TableModel(TERRITOIRE);
        tableModel.addColumn("id").selectId();
        tableModel.addColumn("territoire").select().fieldPath(LABEL_PROPERTY);
        tableModel.addColumn("district").select().fieldPath(PARENT_PROPERTY, LABEL_PROPERTY);
        tableModel.addColumn("province").select().fieldPath(PARENT_PROPERTY, PARENT_PROPERTY, LABEL_PROPERTY);

        TableData tableData = tableService.buildTable(tableModel);

        System.out.println(tableData);
    }

}