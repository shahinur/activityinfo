package org.activityinfo.ui.app.client.page.folder.task;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.tasks.LoadTaskModel;
import org.activityinfo.service.tasks.LoadTaskModelClass;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.request.BlobUploader;
import org.activityinfo.ui.app.client.request.StartTask;
import org.activityinfo.ui.app.client.request.UploadResult;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.html.Icon;

public class ImportFileTask implements Task {
    private Application application;
    private ResourceId ownerId;

    public ImportFileTask(Application application, ResourceId ownerId) {
        this.application = application;
        this.ownerId = ownerId;
    }

    @Override
    public String getLabel() {
        return I18N.CONSTANTS.uploadData();
    }

    @Override
    public Icon getIcon() {
        return FontAwesome.UPLOAD;
    }

    @Override
    public void onClicked() {
        BlobUploader.uploadBlob().then(new AsyncCallback<UploadResult>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Upload failed: " + caught);
            }

            @Override
            public void onSuccess(UploadResult result) {
                LoadTaskModel taskModel = new LoadTaskModel();
                taskModel.setBlobId(result.getBlobId().asString());
                taskModel.setFolderId(ownerId);
                application.getRequestDispatcher().execute(new StartTask(taskModel, LoadTaskModelClass.INSTANCE));
            }
        });
    }
}
