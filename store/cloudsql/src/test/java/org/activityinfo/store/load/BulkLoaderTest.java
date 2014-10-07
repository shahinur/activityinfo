package org.activityinfo.store.load;

import com.google.common.io.ByteSource;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.service.store.FormImportOptions;
import org.activityinfo.service.store.FormImportReader;
import org.activityinfo.service.store.ImportWriter;
import org.activityinfo.service.store.InstanceWriter;
import org.activityinfo.store.hrd.TestingEnvironment;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

@Ignore
public class BulkLoaderTest {

    private ResourceId formClassId = Resources.generateId();

    @Rule
    public TestingEnvironment environment = new TestingEnvironment();
    private final ResourceId nameFieldId = Resources.generateId();


    @Test
    public void test() throws IOException {
        AuthenticatedUser me = environment.getUser();

        FormInstance ws = new FormInstance(environment.generateWorkspaceId(), FolderClass.CLASS_ID);
        ws.setOwnerId(Resources.ROOT_ID);
        ws.set(FolderClass.LABEL_FIELD_ID, "Workspace");

        environment.getStore().create(me, ws.asResource());


        BulkLoader loader = new BulkLoader();
        loader.setWorkspaceId(ws.getId());
        loader.setUser(me);
        loader.setOwnerId(ws.getId());
        loader.setSource(ByteSource.wrap(new byte[0]));
        loader.setReader(new FormReaderStub());

        loader.run();

        // verify that the form class has appeared
        Resource formClass = environment.getStore().get(me, formClassId).getResource();
        assertThat(formClass.getOwnerId(), equalTo(ws.getId()));
        assertThat(formClass.getVersion(), greaterThan(1L));


        // Verify we can read imported instances
        TableModel tableModel = new TableModel(formClassId);
        tableModel.selectField(nameFieldId).as("C1");

        TableData data = environment.getStore().queryTable(environment.getUser(), tableModel);

        System.out.println(data.getColumnView("C1"));

        assertThat(data.getNumRows(), equalTo(10));


    }

    private class FormReaderStub implements FormImportReader {

        @Override
        public void load(FormImportOptions options, InputStream inputStream, ImportWriter importWriter) throws IOException {
            FormClass form = new FormClass(formClassId);
            form.setOwnerId(options.getOwnerId());
            form.setLabel("My Form");
            FormField nameField = new FormField(nameFieldId).setLabel("Name").setType(TextType.INSTANCE);
            form.addElement(nameField);

            InstanceWriter writer = importWriter.createFormClass(form);

            for(int i=0;i!=10;++i) {
                FormInstance instance = new FormInstance(Resources.generateId(), form.getId());
                instance.set(nameField.getId(), "N" + i);
                writer.write(instance);
            }
        }
    }

}