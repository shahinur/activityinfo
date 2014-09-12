package org.activityinfo.ui.vdom.shared.dom;

import com.google.gwt.user.client.Event;

public class TestDomEvent implements DomEvent {

    private DomNode eventTarget;
    private int eventCode;
    private int keyCode;

    private TestDomEvent() {}

    public TestDomEvent(DomNode targetEvent, int eventCode) {
        this.eventCode = eventCode;
    }

    public static TestDomEvent keyEvent(DomNode target, int eventType, int keyCode) {
        TestDomEvent event = new TestDomEvent(target, eventType);
        event.keyCode = keyCode;
        return event;
    }

    public static TestDomEvent onClick(DomNode target) {
        return new TestDomEvent(target, Event.ONCLICK);
    }

    @Override
    public int getTypeInt() {
        return eventCode;
    }

    @Override
    public int getKeyCode() {
        return keyCode;
    }

    @Override
    public void preventDefault() {

    }

    @Override
    public DomNode getEventTarget() {
        return eventTarget;
    }


}
