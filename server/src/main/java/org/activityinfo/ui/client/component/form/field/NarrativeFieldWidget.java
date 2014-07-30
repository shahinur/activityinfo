package org.activityinfo.ui.client.component.form.field;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.client.widget.TextArea;

public class NarrativeFieldWidget implements FormFieldWidget {

    private final TextArea textArea;

    public NarrativeFieldWidget(final ValueUpdater updater) {
        this.textArea = new TextArea();
        this.textArea.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                updater.update(event.getValue());
            }
        });
        this.textArea.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                updater.update(NarrativeFieldWidget.this.textArea.getValue());
            }
        });
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        textArea.setReadOnly(readOnly);
    }

    @Override
    public Promise<Void> setValue(Object value) {
        textArea.setValue((String) value);
        return Promise.done();
    }

    @Override
    public void setType(FieldType type) {

    }

    @Override
    public Widget asWidget() {
        return textArea;
    }
}
