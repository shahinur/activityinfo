package org.activityinfo.ui.app.client.page.pivot.tree;

import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.request.FetchFolder;
import org.activityinfo.ui.app.client.request.FetchResource;
import org.activityinfo.ui.app.client.request.FetchWorkspaces;
import org.activityinfo.ui.flux.store.Status;
import org.activityinfo.ui.flux.store.StoreChangeListener;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.style.tree.TreeModel;
import org.activityinfo.ui.vdom.shared.html.Icon;

import java.util.List;

public class FormSelectionTree implements TreeModel<ResourceNode> {

    private Application application;

    public FormSelectionTree(Application application) {
        this.application = application;
    }

    public Application getApplication() {
        return application;
    }

    @Override
    public boolean isLeaf(ResourceNode node) {
        return node.getClassId().equals(FormClass.CLASS_ID);
    }

    @Override
    public Status<List<ResourceNode>> getRootNodes() {
        return Status.cache(application.getWorkspaceStore().get());
    }

    @Override
    public void requestRootNodes() {
        application.getRequestDispatcher().execute(new FetchWorkspaces());
    }

    @Override
    public Status<List<ResourceNode>> getChildren(ResourceNode parent) {
        return application.getFolderStore().getFolderItems(parent.getId());
    }

    @Override
    public String getLabel(ResourceNode node) {
        return node.getLabel();
    }

    @Override
    public Icon getIcon(ResourceNode node, boolean expanded) {
        if(node.getClassId().equals(FormClass.CLASS_ID)) {
            return FontAwesome.CLIPBOARD;
        } else {
            if(expanded) {
                return FontAwesome.FOLDER_OPEN;
            } else {
                return FontAwesome.FOLDER;
            }
        }
    }

    @Override
    public String getKey(ResourceNode node) {
        return node.getId().asString();
    }



    @Override
    public void requestChildren(ResourceNode node) {
        if (node.getClassId().equals(FolderClass.CLASS_ID)) {
            application.getRequestDispatcher().execute(new FetchFolder(node.getId()));

        } else if (node.getClassId().equals(FolderClass.CLASS_ID)) {
            application.getRequestDispatcher().execute(new FetchResource(node.getId()));
        }
    }

    @Override
    public void addChangeListener(StoreChangeListener listener) {
        application.getWorkspaceStore().addChangeListener(listener);
        application.getFolderStore().addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(StoreChangeListener listener) {
        application.getWorkspaceStore().removeChangeListener(listener);
        application.getFolderStore().removeChangeListener(listener);
    }

}
