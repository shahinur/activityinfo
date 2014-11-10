package org.activityinfo.server.endpoint.odk;

import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.server.endpoint.odk.xform.BindingType;
import org.activityinfo.server.endpoint.odk.xform.BodyElement;
import org.activityinfo.server.endpoint.odk.xform.Input;

class QuantityFieldBuilder implements OdkFormFieldBuilder {
    final private String units;

    QuantityFieldBuilder(QuantityType quantityType) {
        this.units = quantityType.getUnits();
    }

    @Override
    public BindingType getModelBindType() {
        return BindingType.DECIMAL;
    }

    @Override
    public BodyElement createPresentationElement(String ref, String label, String hint) {
        Input input = new Input();
        input.setRef(ref);

        if (units == null) {
            input.setLabel(label);
        } else if (label == null) {
            input.setLabel(units);
        } else {
            input.setLabel(label + " [" + units + ']');
        }
        input.setHint(hint);

        return input;
    }
}
