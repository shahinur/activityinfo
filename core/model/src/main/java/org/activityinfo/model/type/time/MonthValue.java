package org.activityinfo.model.type.time;

import org.activityinfo.model.resource.IsRecord;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.FieldValue;

/**
 * Represents a specific calendar month in the ISO-8601 calendar.
 */
public class MonthValue implements FieldValue, IsRecord {


    private final int year;
    private final int monthOfYear;

    /**
     *
     * @param year  complete calendar year, including century. For example: 1999, 2014
     * @param monthOfYear the month of the year, where January = 1 and December = 12
     */
    public MonthValue(int year, int monthOfYear) {
        assert monthOfYear >= 1 && monthOfYear <= 12;
        this.year = year;
        this.monthOfYear = monthOfYear;
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return MonthType.TYPE_CLASS;
    }


    @Override
    public Record asRecord() {
        return new Record().set("year", year).set("month", monthOfYear);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MonthValue that = (MonthValue) o;

        if (monthOfYear != that.monthOfYear) return false;
        if (year != that.year) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = year;
        result = 31 * result + monthOfYear;
        return result;
    }
}
