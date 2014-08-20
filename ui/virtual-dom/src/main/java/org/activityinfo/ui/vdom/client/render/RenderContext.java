package org.activityinfo.ui.vdom.client.render;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.ui.vdom.shared.tree.VThunk;

public interface RenderContext {

    void attachWidget(Widget widget);

    void detachWidget(Element element);

    void fireUpdate(VThunk thunk);

}
