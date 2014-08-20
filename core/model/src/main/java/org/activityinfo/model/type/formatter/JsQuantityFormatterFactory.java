package org.activityinfo.model.type.formatter;

import com.google.gwt.i18n.client.NumberFormat;
import org.activityinfo.model.type.number.Quantity;

/**
 * Creates QuantityFormatters using the GWT i18n classes.
 */
public class JsQuantityFormatterFactory implements QuantityFormatterFactory {
    @Override
    public QuantityFormatter create() {
        final NumberFormat format = NumberFormat.getDecimalFormat();
        return new QuantityFormatter() {
            @Override
            public String format(Quantity value) {
                return format.format(value.getValue());
            }

            @Override
            public Quantity parse(String valueAsString) {
                return new Quantity(format.parse(valueAsString));
            }
        };
    }
}
