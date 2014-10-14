package org.activityinfo.model.type.time;

import org.activityinfo.model.annotation.RecordBean;
import org.activityinfo.model.record.IsRecord;
import org.activityinfo.model.record.Record;

import javax.annotation.Nonnull;

/**
 * Record that describes
 * a continuous interval between two {@link org.activityinfo.model.type.time.LocalDate}s,
 * starting on {@code startDate}, inclusive, and ending on {@code endDate}, inclusive.
 */
@RecordBean(classId = "_localDateInterval")
public class LocalDateInterval implements IsRecord {

    private LocalDate startDate;
    private LocalDate endDate;

    LocalDateInterval() {
    }

    public LocalDateInterval(@Nonnull LocalDate startDate, @Nonnull LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     *
     * @return the start date, inclusive of this interval
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * @return the end date, inclusive, of this interval
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalDateInterval that = (LocalDateInterval) o;

        if (endDate != null ? !endDate.equals(that.endDate) : that.endDate != null) return false;
        if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = startDate != null ? startDate.hashCode() : 0;
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        return result;
    }

    @Override
    public Record asRecord() {
        return LocalDateIntervalClass.INSTANCE.toRecord(this);
    }
}
