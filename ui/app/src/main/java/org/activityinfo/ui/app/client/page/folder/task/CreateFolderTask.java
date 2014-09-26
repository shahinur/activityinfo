package org.activityinfo.ui.app.client.page.folder.task;

import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.system.Folder;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.request.SaveRequest;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.html.Icon;

public class CreateFolderTask implements Task {

    private Application application;
    private ResourceId ownerId;

    public CreateFolderTask(Application application, ResourceId ownerId) {
        this.application = application;
        this.ownerId = ownerId;
    }

    @Override
    public String getLabel() {
        return I18N.CONSTANTS.newFolder();
    }

    @Override
    public Icon getIcon() {
        return FontAwesome.FOLDER;
    }

    @Override
    public void onClicked() {
        Folder newFolder = new Folder();
        newFolder.setLabel(I18N.CONSTANTS.newFolder());

        Resource resource = Resources.newResource(ownerId, newFolder);

        application.getRequestDispatcher().execute(new SaveRequest(resource));
    }
}
