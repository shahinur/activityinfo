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

import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.flux.store.Store;
import org.activityinfo.ui.flux.store.StoreChangeListener;
import org.activityinfo.ui.style.tree.TreeModel;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

/**
 * @author yuriyz on 9/11/14.
 */
public class NavigationComponent extends VComponent implements StoreChangeListener {

    private final Application application;
    private final TreeModel<ResourceNode> treeModel;
    private NavigationTree treeComponent;

    public NavigationComponent(Application application) {
        this.application = application;
        this.treeModel = new NavigationTreeModel(application);
        this.treeComponent = new NavigationTree(treeModel);
    }

    @Override
    public void onStoreChanged(Store store) {
        render();
    }

    @Override
    public void componentDidMount() {
        application.getWorkspaceStore().addChangeListener(this);
        application.getFolderStore().addChangeListener(this);
    }

    @Override
    protected void componentWillUnmount() {
        application.getWorkspaceStore().removeChangeListener(this);
        application.getFolderStore().addChangeListener(this);
    }

    @Override
    protected VTree render() {
        return treeComponent;
    }
}
