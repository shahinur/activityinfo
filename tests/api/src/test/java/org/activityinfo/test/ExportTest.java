package org.activityinfo.test;

import com.google.common.base.Charsets;
import com.google.common.collect.Iterators;
import com.google.common.io.ByteSource;
import org.activityinfo.model.record.Records;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.tasks.ExportFormTaskModel;
import org.activityinfo.service.tasks.UserTask;
import org.activityinfo.store.test.TestFormClass;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import static org.activityinfo.model.resource.Resources.ROOT_ID;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

public class ExportTest {


    @Test
    public void test() throws InterruptedException, IOException {
        ResourceId workspaceId = Resources.generateId();

        final ActivityInfoTestClient client = new ActivityInfoTestClient(TestConfig.getRootURI());

        Resource workspace = Resources.createResource();
        workspace.setId(workspaceId);
        workspace.setOwnerId(ROOT_ID);
        workspace.setVersion(1);
        workspace.setValue(Records.builder(FolderClass.CLASS_ID)
            .set(FolderClass.LABEL_FIELD_ID.asString(), "Workspace")
            .build());
        client.create(workspace);

        TestFormClass form = new TestFormClass(workspaceId);

        client.create(form.formClass.asResource());

        for(Resource instance : form.instances(10)) {
            client.create(instance);
        }

        ExportFormTaskModel taskModel = new ExportFormTaskModel();
        taskModel.setBlobId(BlobId.generate());
        taskModel.setFilename("my_export.csv");
        taskModel.setFormClassId(form.formClass.getId());

        UserTask task = client.startTask(taskModel);
        client.waitForTask(task.getId(), 30, TimeUnit.SECONDS);

        ByteSource blob = client.getBlob(BlobId.valueOf(taskModel.getBlobId()));

        System.out.print(new String(blob.read()));

        CSVParser parser = new CSVParser(blob.asCharSource(Charsets.UTF_8).openStream(), CSVFormat.DEFAULT);
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

        assertThat(Iterators.size(iterator), equalTo(10));
    }
}
