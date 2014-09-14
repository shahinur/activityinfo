package org.activityinfo.model.type.time;


import com.google.gwt.core.shared.GWT;

import java.util.GregorianCalendar;

/**
 * Encapsulates time/date computations for which we need to provide
 * GWT emulations
 */
class TimeUtils {

    static LocalDate getLastDayOfMonth(MonthValue month) {
        if(GWT.isClient()) {
            int zeroBasedMonthOfYear = month.getMonthOfYear() - 1;
            int lastDay = lastDay(month.getYear(), zeroBasedMonthOfYear);
            return new LocalDate(month.getYear(), month.getMonthOfYear(), lastDay);

        } else {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.set(month.getYear(), month.getMonthOfYear() - 1, 1);

            return new LocalDate(month.getYear(), month.getMonthOfYear(),
                calendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
        }
    }

    private static native int lastDay(int year, int zeroBasedMonth) /*-{
        var d = new Date(year, zeroBasedMonth + 1, 0);
        return d.getDay();
    }-*/;

}
