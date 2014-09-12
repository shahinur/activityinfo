package org.activityinfo.ui.app.client.page.pivot.tree;

import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.store.FolderStore;
import org.activityinfo.ui.app.client.store.WorkspaceStore;
import org.activityinfo.ui.flux.store.Store;
import org.activityinfo.ui.flux.store.StoreChangeListener;
import org.activityinfo.ui.style.Button;
import org.activityinfo.ui.style.ButtonStyle;
import org.activityinfo.ui.style.ClickHandler;
import org.activityinfo.ui.style.Modal;
import org.activityinfo.ui.style.tree.SingleSelectionModel;
import org.activityinfo.ui.style.tree.TreeComponent;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.t;

public class FormChooser extends VComponent implements StoreChangeListener {

    private final Application application;
    private final WorkspaceStore workspaceStore;
    private final FolderStore folderStore;
    private final Modal modal;

    private final Button cancelButton;
    private final Button addButton;

    private final TreeComponent<ResourceNode> tree;
    private final FormSelectionTree treeModel;

    private final SingleSelectionModel selectionModel = new SingleSelectionModel();

    private AcceptHandler<ResourceId> acceptHandler;

    public FormChooser(Application application) {
        this.application = application;
        this.workspaceStore = application.getWorkspaceStore();
        this.folderStore = application.getFolderStore();

        treeModel = new FormSelectionTree(application);
        this.tree = new TreeComponent<>(treeModel, selectionModel);

        this.modal = new Modal();
        this.modal.setTitle(t("Choose source"));

        this.cancelButton = new Button(ButtonStyle.DEFAULT, t(I18N.CONSTANTS.cancel()));
        this.cancelButton.setClickHandler(new ClickHandler() {
            @Override
            public void onClicked() {
                modal.setVisible(false);
            }
        });
        this.addButton = new Button(ButtonStyle.PRIMARY, t(I18N.CONSTANTS.add()));
        this.addButton.setClickHandler(new ClickHandler() {
            @Override
            public void onClicked() {
                onAccepted();
            }
        });
        this.modal.setBody(tree);
        this.modal.setFooter(cancelButton, addButton);
    }


    @Override
    protected void componentDidMount() {
        treeModel.addChangeListener(this);
    }

    @Override
    protected void componentWillUnmount() {
        treeModel.removeChangeListener(this);
    }

    @Override
    protected VTree render() {
        return modal;
    }

    @Override
    public void onStoreChanged(Store store) {
        addButton.setEnabled(selectionModel.hasSelection());
    }

    public void setVisible(boolean visible) {
        modal.setVisible(visible);
    }

    private void onAccepted() {
        if(acceptHandler != null) {
            acceptHandler.onAccepted(ResourceId.valueOf(selectionModel.getSelectedKey()));
        }
        modal.setVisible(false);
    }
}
