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
import org.activityinfo.ui.app.client.page.folder.FolderPlace;
import org.activityinfo.ui.style.tree.TreeComponent;
import org.activityinfo.ui.style.tree.TreeNodeRenderer;
import org.activityinfo.ui.vdom.shared.tree.VTree;

/**
 * @author yuriyz on 9/11/14.
 */
public class NavigationNodeRenderer implements TreeNodeRenderer<ResourceNode> {

    @Override
    public VTree renderNode(final ResourceNode node, final TreeComponent<ResourceNode> tree) {
        NavigationTreeModel model = (NavigationTreeModel) tree.getModel();
        NavNode navNode = new NavNode(model.getApplication().getRouter(), node, tree);
        navNode.setLabel(model.getLabel(node));
        navNode.setTarget(new FolderPlace(node.getId()));
        navNode.setIcon(model.getIcon(node, tree.isExpanded(node)));
        return navNode;
    }
}
