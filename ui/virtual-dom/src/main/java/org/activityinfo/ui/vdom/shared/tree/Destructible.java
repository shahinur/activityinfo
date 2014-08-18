package org.activityinfo.ui.vdom.shared.tree;

/**
 * A VWidget that must be cleaned up after its removal from the DOM.
 */
public interface Destructible {

    void destroy(Object node);
}
