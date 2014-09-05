package org.activityinfo.ui.vdom.shared.dom;

import com.google.gwt.dom.client.Text;

public final class BrowserTextNode extends BrowserDomNode implements DomText {

    protected BrowserTextNode() {}

    @Override
    public void setData(String text) {
        this.<Text>cast().setData(text);
    }

    @Override
    public String getData() {
        return this.<Text>cast().getData();
    }
}
