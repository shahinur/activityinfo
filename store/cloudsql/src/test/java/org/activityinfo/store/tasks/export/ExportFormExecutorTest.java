package org.activityinfo.store.tasks.export;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.system.Folder;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.tasks.ExportFormTaskModel;
import org.activityinfo.store.hrd.TestingEnvironment;
import org.activityinfo.store.tasks.TestingTaskContext;
import org.activityinfo.store.test.TestFormClass;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.Rule;
import org.junit.Test;

import java.io.StringReader;
import java.util.Iterator;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

public class ExportFormExecutorTest {

    @Rule
    public TestingEnvironment environment = new TestingEnvironment();

    @Test
    public void test() throws Exception {

        Folder folder = new Folder();
        folder.setLabel("My Workspace");

        Resource workspace = Resources.newResource(Resources.ROOT_ID, FolderClass.INSTANCE.toRecord(folder));
        environment.getStore().create(environment.getUser(), workspace);

        TestFormClass form = new TestFormClass(workspace.getId());


        environment.getStore().create(environment.getUser(), form.formClass.asResource());

        for (Resource resource : form.instances(10)) {
            environment.getStore().create(environment.getUser(), resource);
        }


        ExportFormTaskModel task = new ExportFormTaskModel();
        task.setBlobId(BlobId.generate());
        task.setFilename("Export.csv");
        task.setFormClassId(form.formClass.getId());

        TestingTaskContext context = new TestingTaskContext(environment);
        ExportFormExecutor executor = new ExportFormExecutor();
        executor.execute(context, task);

        ByteSource byteSource = context.getBlob(BlobId.valueOf(task.getBlobId()));

        String csvText = new String(byteSource.read(), Charsets.UTF_8);

        System.out.println(csvText);

        CSVParser parser = new CSVParser(new StringReader(csvText), CSVFormat.DEFAULT);
        Iterator<CSVRecord> iterator = parser.iterator();

        CSVRecord header = iterator.next();
        assertThat(header, hasItems(
            "Name",
            "Serial Num.",
            "Respondent's age",
            "Gender",
            "Problems facing your village, family - Water",
            "Problems facing your village, family - Education",
            "Problems facing your village, family - Other",
            "Geographic Position - Latitude",
            "Geographic Position - Longitude",
            "Year of Birth",
            "Picture"));
    }
}