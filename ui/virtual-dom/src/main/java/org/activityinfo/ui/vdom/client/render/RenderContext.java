package org.activityinfo.ui.vdom.client.render;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.ui.vdom.shared.dom.DomDocument;
import org.activityinfo.ui.vdom.shared.dom.DomNode;
import org.activityinfo.ui.vdom.shared.tree.VThunk;

public interface RenderContext {

    DomDocument getDocument();

    void attachWidget(Widget widget);

    void detachWidget(Element element);

    void fireUpdate(VThunk thunk);

    void registerEventListener(DomNode node, VThunk thunk);

    void onComponentUnmounted(DomNode node, VThunk w);

}
