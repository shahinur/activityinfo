package org.activityinfo.ui.vdom.shared.tree;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.ui.vdom.shared.dom.DomElement;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;

/**
 * A virtual node that serves a placeholder for a GWT widget.
 */
public abstract class VWidget extends VComponent {

    private Widget widget;

    /**
     * Called when the Widget is added to the tree for the first time.
     *
     * @return a new GWT widget instance.
     */
    public abstract IsWidget createWidget();

    @Override
    public void accept(VTreeVisitor visitor) {
        visitor.visitWidget(this);
    }


    @Override
    protected void componentDidMount() {
        assert widget == null : "component has already been mounted";
        widget = createWidget().asWidget();
        getContext().attachWidget(widget, (DomElement)getDomNode());
    }

    @Override
    protected void componentWillMount() {
        getContext().detachWidget(widget);
        widget = null;
    }

    @Override
    protected VTree render() {
        return new VNode(HtmlTag.DIV);
    }

}
