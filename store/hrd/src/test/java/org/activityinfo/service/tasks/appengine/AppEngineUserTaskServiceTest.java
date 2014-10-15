package org.activityinfo.service.tasks.appengine;

import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.tasks.ExportFormTaskModel;
import org.activityinfo.service.tasks.ExportFormTaskModelClass;
import org.activityinfo.service.tasks.UserTask;
import org.activityinfo.service.tasks.UserTaskStatus;
import org.activityinfo.store.hrd.TestingEnvironment;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class AppEngineUserTaskServiceTest {

    @Rule
    public TestingEnvironment environment = new TestingEnvironment();

    @Test
    public void testExport() {
        String taskId = Resources.generateId().asString();
        ResourceId formClassId = Resources.generateId();
        String blobId = BlobId.generate().asString();

        ExportFormTaskModel taskModel = new ExportFormTaskModel();
        taskModel.setBlobId(blobId);
        taskModel.setFilename("Export.csv");
        taskModel.setFormClassId(formClassId);

        UserTask started = environment.getTaskService().start(me(), taskId,
            ExportFormTaskModelClass.INSTANCE.toRecord(taskModel));

        assertThat(started.getTaskModel(), Matchers.notNullValue());

        UserTask updated = environment.getTaskService().getUserTask(me(), started.getId());

        assertThat(updated.getStatus(), equalTo(UserTaskStatus.RUNNING));
        assertThat(updated.getId(), equalTo(started.getId()));
        assertThat(updated.getTaskModel(), notNullValue());

        ExportFormTaskModel readModel = ExportFormTaskModelClass.INSTANCE.toBean(updated.getTaskModel());
        assertThat(readModel.getBlobId(), equalTo(blobId));
        assertThat(readModel.getFilename(), equalTo("Export.csv"));
        assertThat(readModel.getFormClassId(), Matchers.equalTo(formClassId));

        List<UserTask> running = environment.getTaskService().getUserTasks(me());
        assertThat(running, Matchers.hasSize(1));
        assertThat(running.get(0).getId(), Matchers.equalTo(taskId));

    }

    private AuthenticatedUser me() {
        return environment.getUser();
    }
}