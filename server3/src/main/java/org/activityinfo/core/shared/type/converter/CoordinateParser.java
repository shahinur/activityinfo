package org.activityinfo.core.shared.type.converter;

import org.activityinfo.i18n.shared.I18N;


public class CoordinateParser {


    public static final double MAX_LATITUDE = 90;
    public static final double MAX_LONGITUDE = 180;



    public enum Notation {
        DDd,
        DMd,
        DMS
    }

    /**
     * Provides number formatting & parsing. Extracted from the class to allow
     * for testing.
     */
    public static interface NumberFormatter {

        /*
         * Formats a coordinate as Degree-decimal, with exactly six decimal places and always with a
         * sign prefix. Equivalent the formatting pattern "+0.000000;-0.000000"
         */
        String formatDDd(double value);

        String formatShortFraction(double value);

        String formatInt(double value);

        double parseDouble(String string);
    }

    private static final double MINUTES_PER_DEGREE = 60;
    private static final double SECONDS_PER_DEGREE = 3600;
    private static final double MIN_MINUTES = 0;
    private static final double MAX_MINUTES = 60;
    private static final double MIN_SECONDS = 0;
    private static final double MAX_SECONDS = 60;

    private static final String DECIMAL_SEPARATORS = ".,";

    private final String posHemiChars;
    private final String negHemiChars;

    private double minValue;
    private double maxValue;

    private final String noNumberErrorMessage = I18N.CONSTANTS.noNumber();
    private final String tooManyNumbersErrorMessage = I18N.CONSTANTS.tooManyNumbers();
    private final String noHemisphereMessage = I18N.CONSTANTS.noHemisphere();
    private final String invalidSecondsMessage = I18N.CONSTANTS.invalidMinutes();
    private final String invalidMinutesMessage = I18N.CONSTANTS.invalidMinutes();


    private Notation notation = Notation.DMS;

    private boolean requireSign = true;


    private final NumberFormatter numberFormatter;


    public CoordinateParser(CoordinateAxis axis, NumberFormatter numberFormatter) {
        this.numberFormatter = numberFormatter;
        switch (axis) {
            case LATITUDE:
                posHemiChars = I18N.CONSTANTS.northHemiChars();
                negHemiChars = I18N.CONSTANTS.southHemiChars();
                break;
            case LONGITUDE:
                posHemiChars = I18N.CONSTANTS.eastHemiChars();
                negHemiChars = I18N.CONSTANTS.westHemiChars();
                break;
            default:
                throw new IllegalArgumentException("Axis: " + axis);
        }
    }


    public void setRequireSign(boolean requireSign) {
        this.requireSign = requireSign;
    }

    public Double parse(String value) throws CoordinateFormatException {

        if (value == null) {
            return null;
        }

        StringBuffer[] numbers = new StringBuffer[]{
                new StringBuffer(),
                new StringBuffer(),
                new StringBuffer()};
        int numberIndex = 0;
        int i;

        /*
         * To assure correctness, we're going to insist that the user explicitly
         * enter the hemisphere (+/-/N/S).
         *
         * However, if the bounds dictate that the coordinate is in one
         * hemisphere, then we can assume the sign.
         */
        double sign = maybeInferSignFromBounds();

        for (i = 0; i != value.length(); ++i) {
            char c = value.charAt(i);

            if (isNegHemiChar(c)) {
                sign = -1;
            } else if (isPosHemiChar(c)) {
                sign = 1;
            } else if (isNumberPart(c)) {
                if (numberIndex > 2) {
                    throw new CoordinateFormatException(
                            tooManyNumbersErrorMessage);
                }
                numbers[numberIndex].append(c);
            } else if (numberIndex != 2 && numbers[numberIndex].length() > 0) {
                // advance to the next token on anything else-- whitespace,
                // symbols like ' " ° -- we won't insist that they are used
                // in the right place
                numberIndex++;
            }
        }

        if (sign == 0) {
            if (requireSign) {
                throw new CoordinateFormatException(noHemisphereMessage);
            } else {
                sign = 1;
            }
        }

        return parseCoordinate(numbers) * sign;
    }

    private double maybeInferSignFromBounds() {
        double sign = 0;
        if (maxValue < 0) {
            sign = -1;
        } else if (minValue > 0) {
            sign = +1;
        }
        return sign;
    }

    private boolean isNumberPart(char c) {
        return Character.isDigit(c) || DECIMAL_SEPARATORS.indexOf(c) != -1;
    }

    private boolean isPosHemiChar(char c) {
        return c == '+' || posHemiChars.indexOf(c) != -1;
    }

    private boolean isNegHemiChar(char c) {
        return c == '-' || negHemiChars.indexOf(c) != -1;
    }

    private double parseCoordinate(StringBuffer[] tokens)
            throws CoordinateFormatException {
        if (tokens[0].length() == 0) {
            throw new CoordinateFormatException(noNumberErrorMessage);
        }

        double coordinate = Double.parseDouble(tokens[0].toString());
        notation = Notation.DDd;

        if (tokens[1].length() != 0) {
            double minutes = numberFormatter.parseDouble(tokens[1].toString());
            if (minutes < MIN_MINUTES || minutes > MAX_MINUTES) {
                throw new CoordinateFormatException(invalidMinutesMessage);
            }
            coordinate += minutes / MINUTES_PER_DEGREE;
            notation = Notation.DMd;

        }
        if (tokens[2].length() != 0) {
            double seconds = numberFormatter.parseDouble(tokens[2].toString());
            if (seconds < MIN_SECONDS || seconds > MAX_SECONDS) {
                throw new CoordinateFormatException(invalidSecondsMessage);
            }
            notation = Notation.DMS;
            coordinate += seconds / SECONDS_PER_DEGREE;
        }
        return coordinate;
    }



    /**
     * Formats coordinate value into Degree-Minute-decimal notation
     */
    public String formatAsDMd(double value) {

        double degrees = Math.floor(Math.abs(value));
        double minutes = (Math.abs(value) - degrees);

        StringBuilder sb = new StringBuilder();
        sb.append(numberFormatter.formatInt(Math.abs(degrees))).append("° ");
        sb.append(numberFormatter.formatShortFraction(minutes)).append("' ");
        sb.append(hemisphereChar(value));

        return sb.toString();
    }


    public String formatAsDDd(double coordinate) {
        return numberFormatter.formatDDd(coordinate);
    }

    /**
     * Formats coordinate value into Degree-Minute-Second notation
     */
    public String formatAsDMS(double value) {
        double absv = Math.abs(value);

        double degrees = Math.floor(absv);
        double minutes = ((absv - degrees) * 60.0);
        double seconds = ((minutes - Math.floor(minutes)) * 60.0);
        minutes = Math.floor(minutes);

        StringBuilder sb = new StringBuilder();
        sb.append(numberFormatter.formatInt(Math.abs(degrees))).append("° ");
        sb.append(numberFormatter.formatInt(minutes)).append("' ");
        sb.append(numberFormatter.formatShortFraction(seconds)).append("\" ");
        sb.append(hemisphereChar(value));

        return sb.toString();
    }

    public String format(double coordinate) {
        return format(notation, coordinate);
    }

    public String format(Notation notation, double coordinate) {
        switch (notation) {
            case DDd:
                return formatAsDDd(coordinate);
            case DMd:
                return formatAsDMd(coordinate);
            default:
            case DMS:
                return formatAsDMS(coordinate);
        }
    }

    private char hemisphereChar(double value) {
        if (Math.signum(value) < 0) {
            return negHemiChars.charAt(0);
        } else {
            return posHemiChars.charAt(0);
        }
    }

    public Notation getNotation() {
        return notation;
    }

    public void setNotation(Notation notation) {
        this.notation = notation;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }
}
