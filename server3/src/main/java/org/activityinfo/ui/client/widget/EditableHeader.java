package org.activityinfo.ui.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import org.activityinfo.i18n.shared.I18N;

/**
 * Generic widget that will user to edit any heading.
 *
 * Created by Mithun on 4/3/2014.
 *
 */
public class EditableHeader implements HasValue<String>,IsWidget {

    private final HTMLPanel rootElement;

    interface EditableHeaderUiBinder extends UiBinder<HTMLPanel, EditableHeader> {
    }

    private static EditableHeaderUiBinder ourUiBinder = GWT.create(EditableHeaderUiBinder.class);

    @UiField
    HeadingElement headerElement;
    @UiField
    InlineLabel headerLabel;

    String value;


    public EditableHeader() {
        rootElement = ourUiBinder.createAndBindUi(this);
    }

    @UiHandler("headerLabel")
    public void onClick(ClickEvent event){
        String newValue = Window.prompt(I18N.CONSTANTS.changeHeaderMessage(), value);
        if(newValue != null) {
            setValue(newValue,true);
        }
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        setValue(value,false);
    }

    @Override
    public void setValue(String value, boolean fireEvents) {
        this.value = value;
        headerLabel.setText(value);
        if(fireEvents){
            ValueChangeEvent.fire(this,value);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return rootElement.addHandler(handler,ValueChangeEvent.getType());
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        rootElement.fireEvent(event);
    }

    @Override
    public Widget asWidget() {
        return rootElement;
    }
}