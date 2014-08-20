package org.activityinfo.model.type.converter;


import org.activityinfo.model.type.formatter.JsDateFormatterFactory;
import org.activityinfo.model.type.formatter.JsQuantityFormatterFactory;

/**
 * Creates a converter for a specific field type
 */
public class JsConverterFactory {

    private static ConverterFactory INSTANCE;

    public static ConverterFactory get() {
        if(INSTANCE == null) {
            INSTANCE = new ConverterFactory(
                    new JsQuantityFormatterFactory(),
                    new JsDateFormatterFactory().create(),
                    new JsCoordinateNumberFormatter());
        }
        return INSTANCE;
    }

}
