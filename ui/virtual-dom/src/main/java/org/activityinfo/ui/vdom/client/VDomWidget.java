package org.activityinfo.ui.vdom.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.ui.vdom.shared.tree.VTree;

public class VDomWidget extends Widget {

    public VDomWidget(VTree vtree) {
        setElement(initialRender(vtree));
        sinkEvents(Event.MOUSEEVENTS);
    }

    private Element initialRender(VTree vtree) {
        ElementBuilder builder = new ElementBuilder();
        return builder.createElement(vtree).cast();
    }

}
