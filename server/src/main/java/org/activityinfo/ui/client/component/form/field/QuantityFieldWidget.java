package org.activityinfo.ui.client.component.form.field;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.model.form.FormField;
import org.activityinfo.ui.client.widget.DoubleBox;

public class QuantityFieldWidget implements FormFieldWidget {

    private FlowPanel panel;
    private DoubleBox box;

    public QuantityFieldWidget(final FormField field, final ValueUpdater valueUpdater) {
        box = new DoubleBox();
        box.addValueChangeHandler(new ValueChangeHandler<Double>() {
            @Override
            public void onValueChange(ValueChangeEvent<Double> event) {
                valueUpdater.update(event.getValue());
            }
        });

        panel = new FlowPanel();
        panel.add(box);
        panel.add(new Label(field.getUnit()));
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        box.setReadOnly(readOnly);
    }

    @Override
    public void setValue(Object value) {
        box.setValue((Double) value);
    }

    @Override
    public Widget asWidget() {
        return panel;
    }
}
