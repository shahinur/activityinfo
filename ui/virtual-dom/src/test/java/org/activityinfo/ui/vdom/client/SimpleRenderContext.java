package org.activityinfo.ui.vdom.client;

import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.ui.vdom.shared.dom.BrowserDomDocument;
import org.activityinfo.ui.vdom.client.render.RenderContext;
import org.activityinfo.ui.vdom.shared.dom.DomDocument;
import org.activityinfo.ui.vdom.shared.dom.DomElement;
import org.activityinfo.ui.vdom.shared.dom.DomNode;
import org.activityinfo.ui.vdom.shared.tree.VComponent;

public class SimpleRenderContext implements RenderContext {

    private BrowserDomDocument document = new BrowserDomDocument();

    @Override
    public DomDocument getDocument() {
        return document;
    }


    @Override
    public void fireUpdate(VComponent thunk) {

    }

    @Override
    public void registerEventListener(DomNode node, VComponent thunk) {

    }

    @Override
    public void attachWidget(Widget widget, DomElement container) {

    }

    @Override
    public void detachWidget(Widget widget) {

    }
}
