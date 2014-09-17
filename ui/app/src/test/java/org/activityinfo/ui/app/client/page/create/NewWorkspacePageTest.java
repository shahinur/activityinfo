package org.activityinfo.ui.app.client.page.create;

import com.teklabs.gwt.i18n.server.LocaleProxy;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.model.type.primitive.TextValue;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.form.store.UpdateFieldAction;
import org.activityinfo.ui.app.client.request.SaveRequest;
import org.activityinfo.ui.app.client.store.InstanceState;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class NewWorkspacePageTest {

    @Test
    public void createWorkspace() throws Exception {

        LocaleProxy.initialize();

        MockRemoteStoreService store = new MockRemoteStoreService();
        Application app = new Application(store);
        NewWorkspacePage page = new NewWorkspacePage(app);
        page.componentDidMount();

        InstanceState draft = app.getDraftStore().getWorkspaceDraft();
        assertThat(draft, is(notNullValue()));

        assertThat(app.getDispatcher(), not(nullValue()));

        app.getDispatcher().dispatch(
            new UpdateFieldAction(draft.getInstanceId(),
                FolderClass.LABEL_FIELD_ID, TextValue.valueOf("MyWorkspace")));

        app.getRequestDispatcher().execute(new SaveRequest(draft.getUpdatedResource()));

        Resource created = store.getOnlyCreatedResource();
        assertThat(created.getOwnerId(), equalTo(Resources.ROOT_ID));
        assertThat(created.isString(FolderClass.LABEL_FIELD_ID.asString()), equalTo("MyWorkspace"));

    }
}