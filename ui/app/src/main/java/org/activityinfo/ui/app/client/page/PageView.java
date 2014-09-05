package org.activityinfo.ui.app.client.page;

import org.activityinfo.ui.vdom.shared.tree.VComponent;

public abstract class PageView extends VComponent<PageView> {

    /**
     *
     * @return true if this PageView can handle the given place
     */
    public abstract boolean accepts(Place place);

}
