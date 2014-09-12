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

import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.page.pivot.tree.FormSelectionTree;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.html.Icon;

/**
 * @author yuriyz on 9/11/14.
 */
public class NavigationTreeModel extends FormSelectionTree {

    public NavigationTreeModel(Application application) {
        super(application);
    }

    @Override
    public Icon getIcon(ResourceNode node, boolean expanded) {
        if (node.getClassId().equals(FormClass.CLASS_ID)) {
            return FontAwesome.CLIPBOARD;
        } else {
            return FontAwesome.TH_LARGE;
        }
    }
}
