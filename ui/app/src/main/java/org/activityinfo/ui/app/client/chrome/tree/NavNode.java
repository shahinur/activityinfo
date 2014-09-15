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

import com.google.gwt.user.client.Event;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.ui.app.client.chrome.nav.NavLink;
import org.activityinfo.ui.app.client.store.Router;
import org.activityinfo.ui.flux.store.Status;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.style.tree.TreeComponent;
import org.activityinfo.ui.vdom.shared.VDomLogger;
import org.activityinfo.ui.vdom.shared.dom.DomEvent;
import org.activityinfo.ui.vdom.shared.html.Icon;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.List;

import static org.activityinfo.ui.vdom.shared.html.H.*;

/**
 * @author yuriyz on 9/13/14.
 */
public class NavNode extends NavLink {

    private final TreeComponent<ResourceNode> tree;
    private final NavigationTreeModel treeModel;
    private final ResourceNode node;

    public NavNode(Router router, ResourceNode node, TreeComponent<ResourceNode> tree) {
        super(router);
        this.tree = tree;
        this.node = node;
        this.treeModel = (NavigationTreeModel) tree.getModel();
    }

    @Override
    protected VTree render() {
        return li(style(isActive()),
                link(getTargetSafeUri(), getIcon().render(), space(), span(getLabel()), icon().pullRight().render()));
    }

    private Icon icon() {
        boolean expanded = tree.isExpanded(node);

        if (expanded) {
            Status<List<ResourceNode>> children = treeModel.getChildren(node);
            if (children.isAvailable()) {
                return FontAwesome.MINUS;
            } else {
                return FontAwesome.SPINNER;
            }
        } else {
            return FontAwesome.PLUS;
        }
    }

    @Override
    public int getEventMask() {
        return Event.ONCLICK;
    }

    @Override
    public void onBrowserEvent(DomEvent event) {
        if (event.getTypeInt() == Event.ONCLICK) {

            VDomLogger.event(this, "ONCLICK => " + getPropertiesForDebugging());

            event.preventDefault();
            tree.onLabelClicked(node);
        }
    }
}
