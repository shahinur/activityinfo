package org.activityinfo.ui.client.component.form.field;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.ui.client.widget.DoubleBox;

import java.util.Map;

public class QuantityFieldWidget implements FormFieldWidget {

    private FlowPanel panel;
    private DoubleBox box;
    private final Label unitsLabel;


    public QuantityFieldWidget(final QuantityType type, final ValueUpdater valueUpdater) {
        box = new DoubleBox();
        box.addValueChangeHandler(new ValueChangeHandler<Double>() {
            @Override
            public void onValueChange(ValueChangeEvent<Double> event) {
                valueUpdater.update(event.getValue());
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
    public void setValue(Object value) {
        box.setValue((Double) value);
    }

    @Override
    public void setType(FieldType type) {
        unitsLabel.setText(unitsLabel.getText());
    }

    @Override
    public Widget asWidget() {
        return panel;
    }
}
