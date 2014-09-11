package org.activityinfo.ui.app.client.chrome.tree;
/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.request.FetchFolder;
import org.activityinfo.ui.app.client.request.FetchResource;
import org.activityinfo.ui.flux.store.Status;
import org.activityinfo.ui.flux.store.StoreChangeListener;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.style.tree.TreeModel;
import org.activityinfo.ui.vdom.shared.html.Icon;

import java.util.List;
import java.util.Objects;

/**
 * @author yuriyz on 9/11/14.
 */
public class NavigationTreeModel implements TreeModel<ResourceNode> {

    private final Application application;
    private final List<StoreChangeListener> listeners = Lists.newArrayList();

    private ResourceId selection = null;

    public NavigationTreeModel(Application application) {
        this.application = application;
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
    public Object getKey(ResourceNode node) {
        return node.getId();
    }

    @Override
    public void onExpanded(ResourceNode node) {
        if(node.getClassId().equals(FolderClass.CLASS_ID)) {
            application.getRequestDispatcher().execute(new FetchFolder(node.getId()));

        } else if(node.getClassId().equals(FolderClass.CLASS_ID)) {
            application.getRequestDispatcher().execute(new FetchResource(node.getId()));
        }
    }

    @Override
    public void select(ResourceNode node) {
        if(!Objects.equals(selection, node.getId())) {
            selection = node.getId();
            fireChange();
        }
    }

    private void fireChange() {
        for(StoreChangeListener listener : listeners) {
            listener.onStoreChanged(null);
        }
    }

    @Override
    public boolean isSelected(ResourceNode node) {
        return node.getId().equals(selection);
    }

    @Override
    public void addChangeListener(StoreChangeListener listener) {
        application.getWorkspaceStore().addChangeListener(listener);
        application.getFolderStore().addChangeListener(listener);
        listeners.add(listener);
    }

    @Override
    public void removeChangeListener(StoreChangeListener listener) {
        application.getWorkspaceStore().removeChangeListener(listener);
        application.getFolderStore().removeChangeListener(listener);
        listeners.remove(listener);
    }

    public Optional<ResourceId> getSelection() {
        return Optional.fromNullable(selection);
    }
}
