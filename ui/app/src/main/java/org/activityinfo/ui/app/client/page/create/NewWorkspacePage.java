package org.activityinfo.ui.app.client.page.create;

import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.ui.app.client.form.store.InstanceStore;
import org.activityinfo.ui.app.client.page.Breadcrumb;
import org.activityinfo.ui.app.client.page.PageStore;
import org.activityinfo.ui.app.client.store.Application;
import org.activityinfo.ui.flux.store.LoadingStatus;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.html.Icon;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.List;

public class NewWorkspacePage implements PageStore {

    private final Application app;

    public NewWorkspacePage(Application app) {
        this.app = app;
    }

    private InstanceStore workspace;


    @Override
    public String getPageTitle() {
        return I18N.CONSTANTS.newWorkspace();
    }

    @Override
    public String getPageDescription() {
        return null;
    }

    @Override
    public Icon getPageIcon() {
        return FontAwesome.TH_LARGE;
    }

    @Override
    public List<Breadcrumb> getBreadcrumbs() {
        return null;
    }


    public InstanceStore getInstanceStore() {
        return workspace;
    }

    @Override
    public VTree getView() {
        return new NewWorkspaceView(app.getDispatcher(), this);
    }

    @Override
    public void start() {

        FormInstance newWorkspace = new FormInstance(Resources.generateId(), FolderClass.CLASS_ID);
        newWorkspace.setOwnerId(Resources.ROOT_ID);

        this.workspace = new InstanceStore(app, FolderClass.get());
        this.workspace.newInstance(newWorkspace);

        app.getDispatcher().register(workspace);
    }

    @Override
    public void stop() {
        app.getDispatcher().unregister(workspace);
    }

    @Override
    public LoadingStatus getLoadingStatus() {
        return LoadingStatus.LOADED;
    }

}
