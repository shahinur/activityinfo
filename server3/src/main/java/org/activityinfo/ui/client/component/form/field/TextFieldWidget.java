package org.activityinfo.ui.client.component.form.field;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.client.widget.TextBox;

public class TextFieldWidget implements FormFieldWidget {

    private final TextBox box;

    public TextFieldWidget(final ValueUpdater valueUpdater) {
        this.box = new TextBox();
        this.box.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                valueUpdater.update(event.getValue());
            }
        });
        this.box.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                valueUpdater.update(TextFieldWidget.this.box.getValue());
            }
        });
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        box.setReadOnly(readOnly);
    }

    @Override
    public Promise<Void> setValue(Object value) {
        box.setValue((String) value);
        return Promise.done();
    }

    @Override
    public void setType(FieldType type) {

    }

    @Override
    public Widget asWidget() {
        return box;
    }
}
