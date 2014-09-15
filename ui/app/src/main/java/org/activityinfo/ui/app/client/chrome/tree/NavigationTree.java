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
import org.activityinfo.ui.flux.store.Status;
import org.activityinfo.ui.style.Spinners;
import org.activityinfo.ui.style.tree.SingleSelectionModel;
import org.activityinfo.ui.style.tree.TreeComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.List;

import static org.activityinfo.ui.style.BaseStyles.*;
import static org.activityinfo.ui.vdom.shared.html.H.*;

/**
 * @author yuriyz on 9/11/14.
 */
public class NavigationTree extends TreeComponent<ResourceNode> {

    private final NavigationTreeModel model;

    public NavigationTree(Application application) {
        super(new NavigationTreeModel(application), new SingleSelectionModel());
        this.model = (NavigationTreeModel) getModel();
        setNodeRenderer(new NavigationNodeRenderer());
    }

    @Override
    protected VTree render() {
        Status<List<ResourceNode>> rootNodes = model.getRootNodes();
        if(rootNodes.isAvailable()) {
            return ul(classNames(NAV, NAV_PILLS, NAV_STACKED, NAV_BRACKET), map(rootNodes.get(), getNodeItemRenderer()));
        } else {
            return Spinners.spinner().render();
        }
    }
}
