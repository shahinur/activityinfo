package org.activityinfo.test;

import org.activityinfo.model.record.Records;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.tasks.ExportFormTaskModel;
import org.activityinfo.service.tasks.UserTask;
import org.activityinfo.service.tasks.UserTaskStatus;
import org.activityinfo.store.test.TestFormClass;
import org.junit.Test;

import static org.activityinfo.model.resource.Resources.ROOT_ID;
import static org.junit.Assert.fail;

public class ExportTest {


    @Test
    public void test() throws InterruptedException {
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

        for(int i=0;i!=30;++i) {
            UserTask updated = client.getTaskStatus(task);
            if(updated.getStatus() == UserTaskStatus.COMPLETE) {
                // success!
                return;
            } else if(updated.getStatus() == UserTaskStatus.FAILED) {
                fail("Export FAILED: " + updated.getErrorMessage());
            } else {
                System.out.println(updated.getStatus());
                Thread.sleep(1000);
            }
        }
        fail("Export timed out");
    }
}
