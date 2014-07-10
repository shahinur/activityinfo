package org.activityinfo.ui.client.component.form.field;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.model.type.FieldType;
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
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        textArea.setReadOnly(readOnly);
    }

    @Override
    public void setValue(Object value) {
        textArea.setValue((String) value);
    }

    @Override
    public void setType(FieldType type) {

    }

    @Override
    public Widget asWidget() {
        return textArea;
    }
}
