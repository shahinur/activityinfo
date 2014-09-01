package org.activityinfo.ui.client.component.table.renderer;

public class QuantityRenderer implements ValueRenderer<Double> {

    @Override
    public String asString(Double value) {
        if(value == null || Double.isNaN(value)) {
            return null;
        } else {
            return value.toString();
        }
    }
}
