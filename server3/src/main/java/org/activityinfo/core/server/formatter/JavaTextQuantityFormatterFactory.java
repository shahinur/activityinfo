package org.activityinfo.core.server.formatter;

import org.activityinfo.core.shared.type.formatter.QuantityFormatter;
import org.activityinfo.core.shared.type.formatter.QuantityFormatterFactory;
import org.activityinfo.model.type.number.Quantity;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Creates a formatter for a field using the standard Java API
 */
public class JavaTextQuantityFormatterFactory implements QuantityFormatterFactory {
    @Override
    public QuantityFormatter create() {
        final NumberFormat format = NumberFormat.getNumberInstance();
        return new QuantityFormatter() {
            @Override
            public String format(Quantity value) {
                return format.format(value);
            }

            @Override
            public Quantity parse(String valueAsString) {
                try {
                    // consider strings with '-' not at the start as invalid
                    // e.g. "2012-12-18" is not 2012.0
                    if (valueAsString.indexOf("-") > 1 || valueAsString.contains("/")) {
                        return null;
                    }
                    return new Quantity(format.parse(valueAsString).doubleValue());

                } catch (ParseException e) {
                    return null;
                }
            }
        };
    }
}
