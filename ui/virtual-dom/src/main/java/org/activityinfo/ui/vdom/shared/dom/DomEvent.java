package org.activityinfo.ui.vdom.shared.dom;

import com.google.gwt.core.client.SingleJsoImpl;

/**
 * Interface to a native DOM event that decouples the
 * virtual dom rendering and diffing mechanism from GWTs JSNI objects,
 * which complicate testing.
 *
 * @see com.google.gwt.user.client.Event
 */
@SingleJsoImpl(BrowserDomEvent.class)
public interface DomEvent {


    /**
     * Gets the enumerated type of this event, as defined by {@link com.google.gwt.user.client.Event#ONCLICK},
     * {@link com.google.gwt.user.client.Event#ONMOUSEDOWN}, and so forth.
     *
     * @see com.google.gwt.user.client.Event#getTypeInt()
     *
     * @return the event's enumerated type
     */
    int getTypeInt();

    /**
     * Gets the key code (code associated with the physical key) associated with
     * this event.
     *
     * @return the key code
     * @see com.google.gwt.user.client.Event#getKeyCode()
     * @see com.google.gwt.event.dom.client.KeyCodes
     */
    int getKeyCode();

    /**
     * Prevents the browser from taking its default action for the given event.
     *
     * @see com.google.gwt.user.client.Event#preventDefault()
     */
    void preventDefault();

    /**
     * Returns the element that was the actual target of the given event.
     *
     * @see com.google.gwt.user.client.Event#getEventTarget()
     *
     * @return the target element
     */
    DomNode getEventTarget();
}
