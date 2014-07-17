package org.activityinfo.ui.client.page.config.design.importer;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.legacy.shared.command.CreateEntity;
import org.activityinfo.legacy.shared.command.GetSchema;
import org.activityinfo.legacy.shared.model.ActivityDTO;
import org.activityinfo.legacy.shared.model.AttributeGroupDTO;
import org.activityinfo.legacy.shared.model.SchemaDTO;
import org.activityinfo.legacy.shared.model.UserDatabaseDTO;
import org.activityinfo.server.command.CommandTestCase2;
import org.activityinfo.server.database.OnDataSet;
import org.activityinfo.server.endpoint.rest.SchemaCsvWriter;
import org.activityinfo.ui.client.component.importDialog.data.PastedTable;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/sites-simple1.db.xml")
public class SchemaImporterTest extends CommandTestCase2 {


    @Test
    public void syria() throws IOException {

        UserDatabaseDTO syria = doImport("schema_1064.csv");

        ActivityDTO cash = syria.getActivities().get(0);

        for(AttributeGroupDTO group : cash.getAttributeGroups()) {
            System.out.println(group.getName());
        }

        assertThat(cash.getName(), equalTo("1.Provision of urgent cash assistance"));
        assertThat(cash.getAttributeGroups().size(), equalTo(3));


        SchemaCsvWriter writer = new SchemaCsvWriter();
        writer.write(syria);

        Files.write(writer.toString(), new File("target/syria.csv"), Charsets.UTF_8);
    }

    @Test
    public void southSudan() throws IOException {
        UserDatabaseDTO db = doImport("schema_1321.csv");
        ActivityDTO h2 = db.getActivities().get(0);
        assertThat(h2.getName(), equalTo("H2"));
        assertThat(h2.getCategory(), equalTo("Health"));
    }

    private UserDatabaseDTO doImport(String resourceName) throws IOException {
        String csv = Resources.toString(Resources.getResource(resourceName), Charsets.UTF_8);
        PastedTable source = new PastedTable(csv);


        Map<String, Object> dbProps = Maps.newHashMap();
        dbProps.put("name", "Syria");
        dbProps.put("countryId", 1);

        int databaseId = execute(new CreateEntity("UserDatabase", dbProps)).getNewId();

        SchemaDTO schema = execute(new GetSchema());
        UserDatabaseDTO db = schema.getDatabaseById(databaseId);

        if (db == null) {
            throw new AssertionError("database not created");
        }

        SchemaImporter.WarningTemplates templates = EasyMock.createNiceMock(SchemaImporter.WarningTemplates.class);
        EasyMock.replay(templates);

        SchemaImporter importer = new SchemaImporter(getDispatcher(), db, templates);
        importer.setProgressListener(new SchemaImporter.ProgressListener() {

            @Override
            public void submittingBatch(int batchNumber, int batchCount) {
                System.out.println("Submitting batch " + batchNumber + " of " + batchCount);
            }
        });

        boolean success = importer.parseColumns(source);
        if (success) {
            importer.processRows();
        }

        for (SafeHtml warning : importer.getWarnings()) {
            System.err.println(warning);
        }

        if (!success) {
            throw new AssertionError("there were fatal errors");
        }

        importer.persist(new AsyncCallback<Void>() {

            @Override
            public void onSuccess(Void result) {
                System.out.println("Success");
            }

            @Override
            public void onFailure(Throwable caught) {
                throw new AssertionError(caught);
            }
        });

        return execute(new GetSchema()).getDatabaseById(databaseId);
    }

}
