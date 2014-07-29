package org.activityinfo.core.server.type.converter;

import org.activityinfo.core.shared.type.converter.CoordinateParser;

import java.text.NumberFormat;

public class JreNumberFormats implements CoordinateParser.NumberFormatter {

    @Override
    public double parseDouble(String s) {
        return Double.parseDouble(s);
    }

    @Override
    public String formatDDd(double value) {
        NumberFormat fmt = NumberFormat.getInstance();
        fmt.setMinimumFractionDigits(6);
        fmt.setMaximumFractionDigits(6);
        fmt.setMinimumIntegerDigits(0);

        if (value > 0) {
            return "+" + fmt.format(value);
        } else {
            return fmt.format(value);
        }
    }

    @Override
    public String formatShortFraction(double value) {
        NumberFormat fmt = NumberFormat.getInstance();
        fmt.setMinimumFractionDigits(2);
        fmt.setMaximumFractionDigits(2);
        fmt.setMinimumIntegerDigits(0);

        return fmt.format(value);
    }

    @Override
    public String formatInt(double value) {
        return NumberFormat.getIntegerInstance().format(value);
    }
}
