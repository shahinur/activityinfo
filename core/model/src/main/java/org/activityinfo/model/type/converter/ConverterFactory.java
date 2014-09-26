package org.activityinfo.model.type.converter;

import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.NarrativeType;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.model.type.formatter.DateFormatter;
import org.activityinfo.model.type.formatter.QuantityFormatterFactory;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.model.type.time.LocalDateType;

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

        if(from == TextType.TYPE_CLASS || from == NarrativeType.TYPE_CLASS) {
            return createStringConverter(to);
        } else if(from instanceof QuantityType) {
            return createQuantityConverter(to);
        } else if(from instanceof LocalDateType) {
            return createDateConverter(to);
        } else if(from == ReferenceType.TYPE_CLASS) {
            throw new IllegalArgumentException("Reference fields are handled elsewhere");
        }
        throw new UnsupportedOperationException("Conversion from " + from + " to " + to + " is not supported.");
    }

    private Converter createDateConverter(FieldTypeClass to) {
        if(to == TextType.TYPE_CLASS || to == NarrativeType.TYPE_CLASS) {
            return dateToStringConverter;
        }
        throw new UnsupportedOperationException(to.getId());
    }

    public Converter createQuantityConverter(FieldTypeClass to) {
        if(to == TextType.TYPE_CLASS || to == NarrativeType.TYPE_CLASS) {
            return quantityParser;
        }
        throw new UnsupportedOperationException(to.getId());
    }

    public Converter createStringConverter(FieldTypeClass fieldType) {
        if(fieldType == QuantityType.TYPE_CLASS) {
            return stringToQuantityFormatter;
        } else if(fieldType == LocalDateType.TYPE_CLASS) {
            return StringToDateConverter.INSTANCE;
        } else if(fieldType == TextType.TYPE_CLASS || fieldType == NarrativeType.TYPE_CLASS) {
            return StringParser.INSTANCE;
        }
        throw new UnsupportedOperationException(fieldType.getId());
    }

    public CoordinateParser.NumberFormatter getCoordinateNumberFormatter() {
        return coordinateNumberFormatter;
    }
}
