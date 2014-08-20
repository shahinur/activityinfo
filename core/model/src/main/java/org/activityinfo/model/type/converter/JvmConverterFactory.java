package org.activityinfo.model.type.converter;

import com.google.gwt.core.shared.GwtIncompatible;
import org.activityinfo.model.type.formatter.JavaDateFormatterFactory;
import org.activityinfo.model.type.formatter.JavaTextQuantityFormatterFactory;

/**
 * Provides a converter factory using standard JRE classes
 */
@GwtIncompatible
public class JvmConverterFactory {

    private static ConverterFactory INSTANCE;

    public static ConverterFactory get() {
        if(INSTANCE == null) {
            INSTANCE = new ConverterFactory(
                    new JavaTextQuantityFormatterFactory(),
                    new JavaDateFormatterFactory().create(),
                    new JreNumberFormats());
        }
        return INSTANCE;
    }

}
