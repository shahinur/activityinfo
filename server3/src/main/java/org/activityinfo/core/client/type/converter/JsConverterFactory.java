package org.activityinfo.core.client.type.converter;

import org.activityinfo.core.client.type.formatter.JsDateFormatterFactory;
import org.activityinfo.core.client.type.formatter.JsQuantityFormatterFactory;
import org.activityinfo.core.server.formatter.JavaDateFormatterFactory;
import org.activityinfo.core.server.formatter.JavaTextQuantityFormatterFactory;
import org.activityinfo.core.shared.type.converter.ConverterFactory;

/**
 * Creates a converter for a specific field type
 */
public class JsConverterFactory {

    private static ConverterFactory INSTANCE;

    public static ConverterFactory get() {
        if(INSTANCE == null) {
            INSTANCE = new ConverterFactory(
                    new JsQuantityFormatterFactory(),
                    new JsDateFormatterFactory().create());
        }
        return INSTANCE;
    }

}
