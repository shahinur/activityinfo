package org.activityinfo.model.type.time;

import org.activityinfo.model.record.Record;
import org.activityinfo.model.record.Records;
import org.activityinfo.model.record.IsRecord;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.FieldValue;

/**
 * Represents a specific calendar year in the ISO-8601 calendar.
 */
public class YearValue implements FieldValue, IsRecord, TemporalValue {

    private final int year;

    public YearValue(int year) {
        this.year = year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        YearValue yearValue = (YearValue) o;

        if (year != yearValue.year) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return year;
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return YearType.TYPE_CLASS;
    }

    @Override
    public Record asRecord() {
        return Records.builder().set("year", year).build();
    }

    @Override
    public LocalDateInterval asInterval() {
        return new LocalDateInterval(new LocalDate(year, 1, 1), new LocalDate(year, 12, 31));
    }

    public int getYear() {
        return year;
    }
}
