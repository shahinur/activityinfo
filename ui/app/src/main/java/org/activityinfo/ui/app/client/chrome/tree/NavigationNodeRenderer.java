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

import com.google.gwt.safehtml.shared.UriUtils;
import org.activityinfo.ui.style.BaseStyles;
import org.activityinfo.ui.style.tree.TreeComponent;
import org.activityinfo.ui.style.tree.TreeModel;
import org.activityinfo.ui.style.tree.TreeNodeRenderer;
import org.activityinfo.ui.vdom.shared.html.Icon;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.*;

/**
 * @author yuriyz on 9/11/14.
 */
public class NavigationNodeRenderer<T> implements TreeNodeRenderer<T> {

    @Override
    public VTree renderNode(T node, TreeComponent<T> tree) {
        TreeModel<T> model = tree.getModel();
        Icon icon = model.getIcon(node, tree.isExpanded(node));
        return li(listItemStyle(model.isSelected(node)), link(UriUtils.fromString(""), icon.render(), space(), span(model.getLabel(node))));
    }

    private PropMap listItemStyle(boolean isSelected) {
        if(isSelected) {
            return PropMap.withClasses(BaseStyles.NAV_ACTIVE, BaseStyles.ACTIVE);
        } else {
            return PropMap.EMPTY;
        }
    }
}
