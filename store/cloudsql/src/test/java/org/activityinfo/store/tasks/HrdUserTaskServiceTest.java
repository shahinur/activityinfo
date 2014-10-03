package org.activityinfo.store.tasks;

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
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

public class HrdUserTaskServiceTest {

    @Rule
    public TestingEnvironment environment = new TestingEnvironment();


    @Test
    public void test() {
        UserTask userTask = environment.getTaskService().startTask(me(), "Import Job");
        assertThat(userTask.getStatus(), equalTo(UserTaskStatus.RUNNING));
        assertThat(userTask.getTimeStarted(), lessThan((double)System.currentTimeMillis()));

        // ensure that the task is listed
        List<UserTask> tasks = environment.getTaskService().getUserTasks(me());
        assertThat(tasks, hasSize(1));
        assertThat(tasks.get(0).getStatus(), equalTo(UserTaskStatus.RUNNING));
        assertThat(tasks.get(0).getId(), equalTo(userTask.getId()));

        // update the task
        environment.getTaskService().updateTask(me(), userTask.getId(), UserTaskStatus.COMPLETE);

        // ensure that the task is listed
        List<UserTask> updatedTasks = environment.getTaskService().getUserTasks(me());
        assertThat(updatedTasks, hasSize(1));
        assertThat(updatedTasks.get(0).getStatus(), equalTo(UserTaskStatus.COMPLETE));

        // and that we can fetch by id
        UserTask updated = environment.getTaskService().getUserTask(environment.getUser(), userTask.getId());
        assertThat(updated.getId(), equalTo(updated.getId()));
        assertThat(updated.getStatus(), equalTo(UserTaskStatus.COMPLETE));
    }

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