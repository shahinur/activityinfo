package org.activityinfo.model.type.converter;

import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.formatter.DateFormatter;
import org.activityinfo.model.type.formatter.QuantityFormatterFactory;

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
    private CoordinateParser.NumberFormatter coordinateNumberFormatter;

    public ConverterFactory(QuantityFormatterFactory quantityFormatterFactory,
                            DateFormatter dateFormatter,
                            CoordinateParser.NumberFormatter coordinateNumberFormatter) {
        this.coordinateNumberFormatter = coordinateNumberFormatter;
        quantityParser = new QuantityToStringConverter(quantityFormatterFactory.create());
        stringToQuantityFormatter = new StringToQuantityConverter(quantityFormatterFactory.create());
        dateToStringConverter = new DateToStringConverter(dateFormatter);

    }

    public Converter create(FieldTypeClass from, FieldTypeClass to) {

        if(from == to) {
            return NullConverter.INSTANCE;
        }

        if(from == FieldTypeClass.FREE_TEXT || from == FieldTypeClass.NARRATIVE) {
            return createStringConverter(to);
        } else if(from == FieldTypeClass.QUANTITY) {
            return createQuantityConverter(to);
        } else if(from == FieldTypeClass.LOCAL_DATE) {
            return createDateConverter(to);
        } else if(from == FieldTypeClass.REFERENCE) {
            throw new IllegalArgumentException("Reference fields are handled elsewhere");
        }
        throw new UnsupportedOperationException("Conversion from " + from + " to " + to + " is not supported.");
    }

    private Converter createDateConverter(FieldTypeClass to) {
        if(to == FieldTypeClass.FREE_TEXT || to == FieldTypeClass.NARRATIVE) {
            return dateToStringConverter;
        }
        throw new UnsupportedOperationException(to.getId());
    }

    public Converter createQuantityConverter(FieldTypeClass to) {
        if(to == FieldTypeClass.FREE_TEXT || to == FieldTypeClass.NARRATIVE) {
            return quantityParser;
        }
        throw new UnsupportedOperationException(to.getId());
    }

    public Converter createStringConverter(FieldTypeClass fieldType) {
        if(fieldType == FieldTypeClass.QUANTITY) {
            return stringToQuantityFormatter;
        } else if(fieldType == FieldTypeClass.LOCAL_DATE) {
            return StringToDateConverter.INSTANCE;
        } else if(fieldType == FieldTypeClass.FREE_TEXT || fieldType == FieldTypeClass.NARRATIVE) {
            return StringParser.INSTANCE;
        }
        throw new UnsupportedOperationException(fieldType.getId());
    }

    public CoordinateParser.NumberFormatter getCoordinateNumberFormatter() {
        return coordinateNumberFormatter;
    }
}
