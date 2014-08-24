package org.activityinfo.ui.component.importDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.store.AsyncFormTreeBuilder;
import org.activityinfo.service.store.ResourceLocator;

public class ImportLauncher {

    public static void showImportDialog(final ResourceId formClassId, final ResourceLocator resourceLocator,
                                        final ImportCallback callback) {
        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onFailure(Throwable reason) {
                Window.alert("Failed to load importer: " + reason.getMessage());
            }

            @Override
            public void onSuccess() {
                AsyncFormTreeBuilder treeBuilder = new AsyncFormTreeBuilder(resourceLocator);
                treeBuilder.apply(formClassId).then(new AsyncCallback<FormTree>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert("Failed to load importer: " + caught.getMessage());
                    }

                    @Override
                    public void onSuccess(FormTree result) {
                        ImportPresenter presenter = new ImportPresenter(resourceLocator, result);
                        presenter.getEventBus().addHandler(ImportResultEvent.TYPE, new ImportResultEvent.Handler() {
                            @Override
                            public void onResultChanged(ImportResultEvent event) {
                                callback.importComplete();
                            }
                        });
                        presenter.show();
                    }
                });
            }
        });
    }
}