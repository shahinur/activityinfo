package org.activityinfo.core.server.type.converter;

import org.activityinfo.core.server.formatter.JavaDateFormatterFactory;
import org.activityinfo.core.server.formatter.JavaTextQuantityFormatterFactory;
import org.activityinfo.core.shared.type.converter.ConverterFactory;

/**
 * Provides a converter factory using standard JRE classes
 */
public class JvmConverterFactory {

    private static ConverterFactory INSTANCE;

    public static ConverterFactory get() {
        if(INSTANCE == null) {
            INSTANCE = new ConverterFactory(
                    new JavaTextQuantityFormatterFactory(),
                    new JavaDateFormatterFactory().create());
        }
        return INSTANCE;
    }

}
