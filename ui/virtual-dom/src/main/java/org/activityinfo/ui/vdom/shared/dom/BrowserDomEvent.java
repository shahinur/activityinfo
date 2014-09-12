package org.activityinfo.ui.vdom.shared.dom;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Event;

public final class BrowserDomEvent extends JavaScriptObject implements DomEvent {

    protected BrowserDomEvent() {  }

    @Override
    public int getTypeInt() {
        return asEvent().getTypeInt();
    }

    @Override
    public int getKeyCode() {
        return asEvent().getKeyCode();
    }

    @Override
    public void preventDefault() {
        asEvent().preventDefault();
    }

    @Override
    public DomNode getEventTarget() {
        return asEvent().getEventTarget().<BrowserDomNode>cast();
    }

    private Event asEvent() {
        return this.<Event>cast();
    }

    public static BrowserDomEvent cast(Event event) {
        return event.cast();
    }


}
