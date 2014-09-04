package org.activityinfo.ui.app.client.store;

import com.teklabs.gwt.i18n.server.LocaleProxy;
import org.activityinfo.model.resource.FolderProjection;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.model.type.primitive.TextValue;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.form.store.UpdateFieldAction;
import org.activityinfo.ui.app.client.page.create.MockRemoteStoreService;
import org.activityinfo.ui.app.client.request.SaveRequest;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class FolderStoreTest {

    @Before
    public void setUp() {
        LocaleProxy.initialize();
    }

    @Test
    public void testCacheNewWorkspace() {

        Application app = new Application(new MockRemoteStoreService());

        // Create a new workspace
        InstanceState workspaceDraft = app.getDraftStore().getWorkspaceDraft();
        workspaceDraft.updateField(new UpdateFieldAction(FolderClass.LABEL_FIELD_ID, TextValue.valueOf("My Workspace")));
        app.getRequestDispatcher().execute(new SaveRequest(workspaceDraft.getUpdatedResource()));

        // Verify that the folder store is updated
        Status<FolderProjection> folderProjectionStatus = app.getFolderStore().get(workspaceDraft.getInstanceId());
        assertTrue(folderProjectionStatus.isAvailable());
        assertThat(folderProjectionStatus.get().getRootNode().getLabel(), equalTo("My Workspace"));

    }

}