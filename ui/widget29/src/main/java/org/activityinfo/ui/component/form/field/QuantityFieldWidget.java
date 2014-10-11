package org.activityinfo.ui.component.form.field;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.number.Quantity;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.widget.form.DoubleBox;

public class QuantityFieldWidget implements FormFieldWidget<Quantity> {

    private FlowPanel panel;
    private DoubleBox box;
    private final Label unitsLabel;


    public QuantityFieldWidget(final QuantityType type, final ValueUpdater<Quantity> valueUpdater) {
        box = new DoubleBox();
        box.addValueChangeHandler(new ValueChangeHandler<Double>() {
            @Override
            public void onValueChange(ValueChangeEvent<Double> event) {
                valueUpdater.update(new Quantity(event.getValue(), type.getUnits()));
            }
        });

        unitsLabel = new Label(type.getUnits());

        panel = new FlowPanel();
        panel.add(box);
        panel.add(unitsLabel);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        box.setReadOnly(readOnly);
    }

    @Override
    public Promise<Void> setValue(Quantity value) {
        box.setValue(value != null ? value.getValue() : null);
        return Promise.done();
    }

    @Override
    public void clearValue() {
        box.setValue(null);
    }

    @Override
    public void setType(FieldType type) {
        unitsLabel.setText(((QuantityType) type).getUnits());
    }

    @Override
    public Widget asWidget() {
        return panel;
    }
}
