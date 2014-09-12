package org.activityinfo.ui.vdom.shared.dom;


import com.google.gwt.core.client.SingleJsoImpl;

/**
 * Interface to a native DOM text node that decouples the
 * virtual dom rendering and diffing mechanism from GWTs JSNI objects,
 * which complicate testing.
 *
 * @see com.google.gwt.dom.client.Text
 */
@SingleJsoImpl(BrowserDomNode.class)
public interface DomText extends DomNode {

    void setData(String text);

    /**
     * The character data of this text node.
     */
    String getData();
}
