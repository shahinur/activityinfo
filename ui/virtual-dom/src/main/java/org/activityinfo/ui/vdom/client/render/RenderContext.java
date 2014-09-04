package org.activityinfo.ui.vdom.client.render;

import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.ui.vdom.shared.dom.DomDocument;
import org.activityinfo.ui.vdom.shared.dom.DomElement;
import org.activityinfo.ui.vdom.shared.dom.DomNode;
import org.activityinfo.ui.vdom.shared.tree.VComponent;

public interface RenderContext {

    DomDocument getDocument();

    void attachWidget(Widget widget, DomElement container);

    void detachWidget(Widget widget);

    void fireUpdate(VComponent thunk);

    void registerEventListener(DomNode node, VComponent thunk);

}
