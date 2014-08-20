package org.activityinfo.ui.app.client.page.form;

import org.activityinfo.ui.style.BaseStyles;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.html.Icon;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.*;

public class ViewSelector {

//    <ul class="filemanager-options">
//    <li>
//    <div class="ckbox ckbox-default">
//    <input id="selectall" value="1" type="checkbox">
//    <label for="selectall">Select All</label>
//    </div>
//    </li>
//    <li>
//    <a href="" class="itemopt disabled"><i class="fa fa-envelope-o"></i> Email</a>
//    </li>
//    <li>
//    <a href="" class="itemopt disabled"><i class="fa fa-download"></i> Download</a>
//    </li>
//    <li>
//    <a href="" class="itemopt disabled"><i class="fa fa-pencil"></i> Edit</a>
//    </li>
//    <li>
//    <a href="" class="itemopt disabled"><i class="fa fa-trash-o"></i> Delete</a>
//    </li>
//    <li class="filter-type">
//    Show:
//    <a href="all" class="active">All</a>
//    <a href="document">Documents</a>
//    <a href="audio">Audio</a>
//    <a href="image">Images</a>
//    <a href="video">Videos</a>
//    </li>
//
//    </ul>


    public static VTree render(FormPage page) {
        return ul(classNames(BaseStyles.NAV, BaseStyles.NAV_PILLS),
                viewChoice(page, FormViewType.OVERVIEW),
                viewChoice(page, FormViewType.TABLE),
                viewChoice(page, FormViewType.DESIGN));
    }

    private static VNode viewChoice(FormPage page, FormViewType view) {
        PropMap props = new PropMap();
        if(page.getViewType() == view) {
            props.setClass(BaseStyles.ACTIVE);
        }
        return li(props, a(href(page.viewUri(view)), viewIcon(view).render(), t(" " + viewLabel(view))));
    }

    private static Icon viewIcon(FormViewType view) {
        switch (view) {
            case OVERVIEW:
                return FontAwesome.HOME;
            case TABLE:
                return FontAwesome.TABLE;
            case DESIGN:
                return FontAwesome.PENCIL;
        }
        throw new IllegalArgumentException();
    }

    private static String viewLabel(FormViewType view) {
        switch(view) {
            case OVERVIEW:
                return "Overview";
            case TABLE:
                return "Table";
            case DESIGN:
                return "Design";
        }
        throw new IllegalArgumentException();
    }
}
