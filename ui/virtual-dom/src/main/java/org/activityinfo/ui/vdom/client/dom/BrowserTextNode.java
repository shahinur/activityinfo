package org.activityinfo.ui.vdom.client.dom;

import com.google.gwt.dom.client.Text;
import org.activityinfo.ui.vdom.shared.dom.DomText;

public final class BrowserTextNode extends BrowserDomNode implements DomText {

    protected BrowserTextNode() {}

    @Override
    public void setData(String text) {
        this.<Text>cast().setData(text);
    }
}
