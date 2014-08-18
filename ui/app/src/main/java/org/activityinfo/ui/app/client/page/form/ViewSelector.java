package org.activityinfo.ui.app.client.page.form;

import org.activityinfo.ui.style.BaseStyles;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.html.Icon;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VThunk;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.*;

public class ViewSelector extends VThunk {

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

    private FormPage page;

    public ViewSelector(FormPage page) {
        this.page = page;
    }

    @Override
    protected VTree render(VThunk previous) {
        return ul(className(BaseStyles.FILEMANAGER_OPTIONS),
                viewChoice(page, FormViewType.OVERVIEW),
                viewChoice(page, FormViewType.TABLE),
                viewChoice(page, FormViewType.DESIGN));
    }

    private VNode viewChoice(FormPage page, FormViewType view) {
        return li(a(href(page.viewUri(view)), viewIcon(view).render(), t(" " + viewLabel(view))));
    }

    private Icon viewIcon(FormViewType view) {
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

    private String viewLabel(FormViewType view) {
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
