package org.activityinfo.ui.app.client.form.control;

import com.google.gwt.user.client.Event;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.type.primitive.TextValue;
import org.activityinfo.ui.app.client.form.store.UpdateFieldAction;
import org.activityinfo.ui.flux.dispatcher.Dispatcher;
import org.activityinfo.ui.style.BaseStyles;
import org.activityinfo.ui.vdom.shared.dom.DomElement;
import org.activityinfo.ui.vdom.shared.dom.DomEvent;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

public class TextControl extends VComponent<TextControl> {

    private final Dispatcher dispatcher;
    private final FormField field;

    public TextControl(Dispatcher dispatcher, FormField field) {
        this.dispatcher = dispatcher;
        this.field = field;
    }

    @Override
    protected VTree render() {
        return new VNode(HtmlTag.INPUT, PropMap.withClasses(BaseStyles.FORM_CONTROL).set("type", "text"));
    }

    @Override
    public int getEventMask() {
        return Event.ONCHANGE;
    }

    @Override
    public void onBrowserEvent(DomEvent event) {
        if(event.getTypeInt() == Event.ONCHANGE) {
            DomElement element = (DomElement)event.getEventTarget();
            String value = element.getInputValue();
            dispatcher.dispatch(new UpdateFieldAction(field.getId(), TextValue.valueOf(value)));
        }
    }
}
