package org.activityinfo.ui.vdom.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.ui.vdom.client.dom.BrowserDomDocument;
import org.activityinfo.ui.vdom.client.render.RenderContext;
import org.activityinfo.ui.vdom.shared.dom.DomDocument;
import org.activityinfo.ui.vdom.shared.tree.VThunk;

public class SimpleRenderContext implements RenderContext {

    private BrowserDomDocument document = new BrowserDomDocument();

    @Override
    public DomDocument getDocument() {
        return document;
    }

    @Override
    public void attachWidget(Widget widget) {

    }

    @Override
    public void detachWidget(Element element) {

    }

    @Override
    public void fireUpdate(VThunk thunk) {

    }
}
