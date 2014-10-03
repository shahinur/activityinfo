package org.activityinfo.ui.app.client.chrome.tasks;

import org.activityinfo.promise.Promise;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.tasks.ExportFormTaskModel;
import org.activityinfo.service.tasks.ExportFormTaskModelClass;
import org.activityinfo.service.tasks.UserTask;
import org.activityinfo.ui.app.client.TestScenario;
import org.activityinfo.ui.app.client.request.StartTask;
import org.activityinfo.ui.vdom.shared.dom.TestRenderContext;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class TaskDropdownMenuTest {

    @Test
    public void failedTaskStart() {
        TestScenario scenario = new TestScenario();

        ExportFormTaskModel taskModel = new ExportFormTaskModel();
        taskModel.setFilename("Export.csv");
        taskModel.setBlobId(BlobId.generate());

        // If we fail to send the task to the server, it should
        // be still added to the store so the user can get the feedback.
        scenario.remoteRequestsShouldFail(true);

        // Submit the request...
        Promise<UserTask> request = scenario.request(new StartTask(taskModel, ExportFormTaskModelClass.INSTANCE));
        assertThat(request.getState(), equalTo(Promise.State.REJECTED));

        // Ensure that it appears
        assertThat(scenario.application().getTaskStore().getTasks(), hasSize(1));
    }

    @Test
    public void updateDropdown() {
        TestScenario scenario = new TestScenario();

        ExportFormTaskModel taskModel = new ExportFormTaskModel();
        taskModel.setFilename("Export.csv");
        taskModel.setBlobId(BlobId.generate());

        // Submit request
        Promise<UserTask> request = scenario.request(new StartTask(taskModel, ExportFormTaskModelClass.INSTANCE));
        assertThat(request.getState(), equalTo(Promise.State.FULFILLED));

        assertThat(scenario.application().getTaskStore().getTasks(), hasSize(1));

        // Render TaskDropDown

        TaskDropdownMenu menu = new TaskDropdownMenu(scenario.application());
        TestRenderContext page = scenario.page();
        page.render(menu);

        page.assertTextIsPresent("Export.csv");
        page.dumpDom();
    }
}