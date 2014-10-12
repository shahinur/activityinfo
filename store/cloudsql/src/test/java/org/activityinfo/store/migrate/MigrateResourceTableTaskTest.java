package org.activityinfo.store.migrate;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.mysql.jdbc.Driver;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.system.Folder;
import org.activityinfo.service.DeploymentConfiguration;
import org.activityinfo.store.hrd.TestingEnvironment;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class MigrateResourceTableTaskTest {

    @Rule
    public TestingEnvironment environment = new TestingEnvironment();

    @Test
    public void parseForm() throws IOException {

        MigrateResourceTableTask task = new MigrateResourceTableTask(new DeploymentConfiguration(new Properties()));
        Record record = task.parseRecord(Resources.toString(Resources.getResource("qis-form.json"), Charsets.UTF_8));

        Resource resource = org.activityinfo.model.resource.Resources.createResource();
        resource.setId(ResourceId.valueOf("a1127"));
        resource.setOwnerId(ResourceId.valueOf("d524"));
        resource.setValue(record);

        FormClass formClass = FormClass.fromResource(resource);

        for(FormField field : formClass.getFields()) {
            System.out.println(field.getLabel());
        }
    }

    @Test
    public void runMigration() {
        Properties properties = new Properties();
        properties.setProperty(MigrateResourceTableTask.DRIVER_CLASS_PROPERTY, Driver.class.getName());
        properties.setProperty(MigrateResourceTableTask.CONNECTION_URL_PROPERTY,
            "jdbc:mysql://localhost:3306/activityinfo?user=root&password=root");

        Folder workspace = new Folder();
        workspace.setLabel("Workspace");
        Resource workspaceResource = org.activityinfo.model.resource.Resources.newResource(org.activityinfo.model.resource.Resources.ROOT_ID, workspace);
        environment.getStore().create(environment.getUser(), workspaceResource);

        Folder folder = new Folder();
        folder.setLabel("QIS");
        Resource folderResource = org.activityinfo.model.resource.Resources.newResource(workspaceResource.getId(), folder);
        environment.getStore().create(environment.getUser(), folderResource);


        MigrateResourceTableTask task = new MigrateResourceTableTask(new DeploymentConfiguration(properties));
        task.setDatabaseId(524);

        int count = task.run();
        System.out.println("count = " + count);

        assertThat(task.run(), equalTo(0));
    }
}