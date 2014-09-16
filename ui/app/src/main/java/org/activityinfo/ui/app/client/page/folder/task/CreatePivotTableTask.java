package org.activityinfo.ui.app.client.page.folder.task;

import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.model.analysis.PivotTableModel;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.action.CreateDraft;
import org.activityinfo.ui.app.client.page.ResourcePlace;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.html.Icon;

public class CreatePivotTableTask implements Task {

    private Application application;
    private ResourceId ownerId;

    public CreatePivotTableTask(Application application, ResourceId ownerId) {
        this.application = application;
        this.ownerId = ownerId;
    }

    @Override
    public String getLabel() {
        return I18N.CONSTANTS.newPivotTable();
    }

    @Override
    public Icon getIcon() {
        return FontAwesome.TABLE;
    }

    @Override
    public void onClicked() {
        Resource resource = Resources.createResource();
        resource.setId(Resources.generateId());
        resource.setOwnerId(ownerId);
        resource.set("classId", PivotTableModel.CLASS_ID.asString());

        application.getDispatcher().dispatch(new CreateDraft(resource));
        new ResourcePlace(resource.getId()).navigateTo(application);
    }
}
