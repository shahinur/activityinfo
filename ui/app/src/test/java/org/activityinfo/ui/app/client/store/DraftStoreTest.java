package org.activityinfo.ui.app.client.store;

import com.teklabs.gwt.i18n.server.LocaleProxy;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.model.type.primitive.TextValue;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.form.store.UpdateFieldAction;
import org.activityinfo.ui.app.client.page.create.MockRemoteStoreService;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class DraftStoreTest {

    @Before
    public void setUp() {
        LocaleProxy.initialize();
    }

    @Test
    public void test() {
        Application application = new Application(new MockRemoteStoreService());
        InstanceState workspaceDraft = application.getDraftStore().getWorkspaceDraft();
        workspaceDraft.updateField(new UpdateFieldAction(FolderClass.LABEL_FIELD_ID, TextValue.valueOf("label")));

        Resource updatedResource = workspaceDraft.getUpdatedResource();
        assertThat(updatedResource.getOwnerId(), equalTo(Resources.ROOT_ID));
    }
}