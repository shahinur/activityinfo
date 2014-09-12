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
import org.activityinfo.ui.app.client.chrome.nav.NavLink;
import org.activityinfo.ui.app.client.page.folder.FolderPlace;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.style.tree.TreeComponent;
import org.activityinfo.ui.style.tree.TreeNodeRenderer;
import org.activityinfo.ui.vdom.shared.html.Icon;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.*;

/**
 * @author yuriyz on 9/11/14.
 */
public class NavigationNodeRenderer implements TreeNodeRenderer<ResourceNode> {

    @Override
    public VTree renderNode(final ResourceNode node, final TreeComponent<ResourceNode> tree) {
        final NavigationTreeModel model = (NavigationTreeModel) tree.getModel();

        final boolean expanded = tree.isExpanded(node);

        NavLink label = new NavLink(model.getApplication().getRouter()) {
                @Override
                protected VTree render() {
                    Icon icon = expanded ? FontAwesome.MINUS : FontAwesome.PLUS;;
                    return li(style(isActive()),
                            link(getTargetSafeUri(), getIcon().render(), space(), span(getLabel()), icon.pullRight().render()));
                }
        };
        label.setLabel(model.getLabel(node));
        label.setTarget(new FolderPlace(node.getId()));
        label.setIcon(model.getIcon(node, expanded));

        return label;
    }
}
