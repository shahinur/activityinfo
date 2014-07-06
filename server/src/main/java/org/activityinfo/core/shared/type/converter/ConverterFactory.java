package org.activityinfo.core.shared.type.converter;

import org.activityinfo.model.form.FormFieldType;
import org.activityinfo.core.shared.type.formatter.DateFormatter;
import org.activityinfo.core.shared.type.formatter.QuantityFormatterFactory;

import java.util.logging.Logger;

/**
 * Provides Converters between supported types.
 *
 *
 *
 */
public class ConverterFactory {


    private static final Logger LOGGER = Logger.getLogger(ConverterFactory.class.getName());

    private final DateToStringConverter dateToStringConverter;
    private final QuantityToStringConverter quantityParser;
    private final StringToQuantityConverter stringToQuantityFormatter;

    public ConverterFactory(QuantityFormatterFactory quantityFormatterFactory, DateFormatter dateFormatter) {
        quantityParser = new QuantityToStringConverter(quantityFormatterFactory.create());
        stringToQuantityFormatter = new StringToQuantityConverter(quantityFormatterFactory.create());
        dateToStringConverter = new DateToStringConverter(dateFormatter);

    }

    public Converter createSilently(FormFieldType from, FormFieldType to) {
        try {
            return create(from, to);
        } catch (Exception e) {
            LOGGER.warning("Unable to create converter from " + from.name() + " to " + to.name() + "" +
                    " (it's not supported or is otherwise illegal)");
            return null;
        }
    }

    public Converter create(FormFieldType from, FormFieldType to) {
        switch (from) {
            case FREE_TEXT:
            case NARRATIVE:
                return createStringConverter(to);
            case QUANTITY:
                return createQuantityConverter(to);
            case LOCAL_DATE:
                return createDateConverter(to);
            case GEOGRAPHIC_POINT:
                return createGeographicPointConverter(to);
            case REFERENCE:
                throw new IllegalArgumentException("Reference fields are handled elsewhere");
        }
        throw new UnsupportedOperationException("Conversion from " + from.name() + " to " + to.name() + " is not supported.");
    }

    private Converter createGeographicPointConverter(FormFieldType to) {
        switch (to) {
            case GEOGRAPHIC_POINT:
                return NullConverter.INSTANCE;
        }
        throw new UnsupportedOperationException(to.name());
    }

    private Converter createDateConverter(FormFieldType to) {
        switch (to) {
            case FREE_TEXT:
            case NARRATIVE:
                return dateToStringConverter;
            case LOCAL_DATE:
                return NullConverter.INSTANCE;
        }
        throw new UnsupportedOperationException(to.name());
    }

    public Converter createQuantityConverter(FormFieldType to) {
        switch (to) {
            case QUANTITY:
                return NullConverter.INSTANCE;
            case FREE_TEXT:
            case NARRATIVE:
                return quantityParser;
            case LOCAL_DATE:
            case GEOGRAPHIC_POINT:
            case REFERENCE:
                throw new UnsupportedOperationException("Conversion from QUANTITY to " + to.name() + " is not supported.");
        }
        throw new UnsupportedOperationException(to.name());
    }

    public Converter createStringConverter(FormFieldType fieldType) {
        switch (fieldType) {
            case QUANTITY:
                return stringToQuantityFormatter;
            case NARRATIVE:
            case FREE_TEXT:
                return NullConverter.INSTANCE;
            case REFERENCE:
                throw new IllegalArgumentException("Reference fields are handled elsewhere");
            case LOCAL_DATE:
                return StringToDateConverter.INSTANCE;
            case GEOGRAPHIC_POINT:
        }
        throw new UnsupportedOperationException(fieldType.name());
    }
}
