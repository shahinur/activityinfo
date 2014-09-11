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

import com.google.gwt.safehtml.shared.SafeUri;
import org.activityinfo.ui.app.client.chrome.nav.DefaultNavLinkRenderer;
import org.activityinfo.ui.app.client.chrome.nav.NavLink;
import org.activityinfo.ui.app.client.chrome.nav.NavLinkRenderer;
import org.activityinfo.ui.app.client.store.Router;
import org.activityinfo.ui.style.BaseStyles;
import org.activityinfo.ui.vdom.shared.html.H;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.*;

/**
 * @author yuriyz on 9/11/14.
 */
public class DivNavLinkRenderer implements NavLinkRenderer {

    @Override
    public VTree render(NavLink navLink) {
        SafeUri uri = navLink.getTarget() == null ? DefaultNavLinkRenderer.DEFAULT_URL : Router.uri(navLink.getTarget());
        return H.li(
                style(navLink.isActive()),
                link(uri, navLink.getIcon().render(), space(), span(navLink.getLabel()))
        );
    }

    private PropMap style(boolean isActive) {
        if (isActive) {
            return H.classNames(BaseStyles.NAV_ACTIVE, BaseStyles.ACTIVE, BaseStyles.LABEL_SUCCESS);
        } else {
            return PropMap.EMPTY;
        }
    }
}
