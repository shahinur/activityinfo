package org.activityinfo.ui.app.client;

import com.teklabs.gwt.i18n.server.LocaleProxy;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.IsResource;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.store.test.TestResourceStore;
import org.activityinfo.ui.app.client.request.FetchWorkspaces;
import org.activityinfo.ui.app.client.request.Request;
import org.activityinfo.ui.app.client.request.TestRemoteStoreService;
import org.activityinfo.ui.vdom.shared.dom.TestRenderContext;

public class TestScenario {

    private final TestRemoteStoreService remoteService;
    private final Application application;
    private final TestResourceStore store;
    private final AuthenticatedUser user = new AuthenticatedUser("", 1, "");

    private final TestRenderContext renderContext;

    public TestScenario() {

        LocaleProxy.initialize();

        this.store = new TestResourceStore();
        this.remoteService = new TestRemoteStoreService(store);
        this.application = new Application(remoteService);

        this.renderContext = new TestRenderContext();
    }

    public TestFolder createWorkspace(String label) {
        FormInstance instance = new FormInstance(Resources.generateId(), FolderClass.CLASS_ID);
        instance.setOwnerId(Resources.ROOT_ID);
        instance.set(FolderClass.LABEL_FIELD_ID, label);
        create(instance);
        return new TestFolder(this, instance.getId());
    }

    public void create(IsResource instance) {
        store.create(user, instance.asResource());
    }

    public Application application() {
        return application;
    }

    public TestScenario fetchWorkspaces() {
        application.getRequestDispatcher().execute(new FetchWorkspaces());
        return this;
    }

    public TestRenderContext page() {
        return renderContext;
    }

    public void request(Request request) {
        application.getRequestDispatcher().execute(request);
    }
}
