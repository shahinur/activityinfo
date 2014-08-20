package org.activityinfo.model.type.converter;

import com.google.gwt.i18n.client.NumberFormat;

/**
 * Alternate implementation of CoordinateFormatter that
 * is translatable to JavaScript
 */
public class CoordinateFormatter {
    private NumberFormat dddFormat;
    private NumberFormat shortFracFormat;
    private NumberFormat intFormat;

    public CoordinateFormatter() {
        dddFormat = NumberFormat.getFormat("+0.000000;-0.000000");
        shortFracFormat = NumberFormat.getFormat("0.00");
        intFormat = NumberFormat.getFormat("0");
    }


    public String formatDDd(double value) {
        return dddFormat.format(value);
    }

    public String formatShortFraction(double value) {
        return shortFracFormat.format(value);
    }

    public String formatInt(double value) {
        return intFormat.format(value);
    }

    public double parseDouble(String string) {
        return Double.parseDouble(string);
    }

}
