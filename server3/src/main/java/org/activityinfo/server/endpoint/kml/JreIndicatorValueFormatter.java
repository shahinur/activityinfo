package org.activityinfo.server.endpoint.kml;

import org.activityinfo.legacy.shared.type.IndicatorValueFormatter;

import java.text.DecimalFormat;

public class JreIndicatorValueFormatter implements IndicatorValueFormatter {
    @Override
    public String format(Double value) {
        return new DecimalFormat("#,##0.####").format(value);
    }
}