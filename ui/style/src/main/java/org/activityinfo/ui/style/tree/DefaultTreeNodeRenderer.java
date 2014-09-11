package org.activityinfo.ui.style.tree;
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

import org.activityinfo.ui.vdom.shared.html.CssClass;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.li;

/**
 * @author yuriyz on 9/11/14.
 */
public class DefaultTreeNodeRenderer<T> implements TreeNodeRenderer<T> {

    @Override
    public VTree renderNode(T node, TreeComponent<T> tree) {

        TreeModel<T> model = tree.getModel();
        boolean expanded = tree.isExpanded(node);
        boolean selected = model.isSelected(node);

        TreeNodeIcon<T> icon = new TreeNodeIcon<>(tree, node, model.getIcon(node, expanded));
        TreeNode<T> label = new TreeNode<>(tree, node, model.getLabel(node));

        PropMap props = new PropMap();
        if(selected) {
            props.setClass(CssClass.valueOf("selected"));
        }

        return li(props,
                tree.background(),
                new VNode(HtmlTag.SPAN, PropMap.withClasses("node-container"),
                        icon, label),
                tree.renderChildren(node, expanded));
    }
}
