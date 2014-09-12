package org.activityinfo.ui.app.client.store;

import org.activityinfo.model.form.FormClass;
import org.activityinfo.ui.app.client.TestFolder;
import org.activityinfo.ui.app.client.TestFormClass;
import org.activityinfo.ui.app.client.TestScenario;
import org.activityinfo.ui.app.client.request.FetchResource;
import org.activityinfo.ui.flux.store.Status;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class ResourceStoreTest {

    @Test
    public void cacheFetchedResource() {

        TestScenario scenario = new TestScenario();
        TestFolder workspace = scenario.createWorkspace("My Workspace");
        TestFormClass form = workspace.newFormClass("Form 1040").addTextField("SS Number").create();

        // Request the form
        scenario.request(new FetchResource(form.getId()));

        // Verify that the resource becomes available
        Status<FormClass> requestedForm = scenario.application().getResourceStore().getFormClass(form.getId());

        assertThat(requestedForm.isAvailable(), equalTo(true));
    }

}