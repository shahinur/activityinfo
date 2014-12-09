package org.activityinfo.model.type.time;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class TimeUtilsTest {

    @Test
    public void december() {
        LocalDate lastDay = TimeUtils.getLastDayOfMonth(new MonthValue(2014, 12));
        assertThat(lastDay.getYear(), equalTo(2014));
        assertThat(lastDay.getMonthOfYear(), equalTo(12));
        assertThat(lastDay.getDayOfMonth(), equalTo(31));
    }

    @Test
    public void february() {
        LocalDate lastDay = TimeUtils.getLastDayOfMonth(new MonthValue(2014, 2));
        assertThat(lastDay.getYear(), equalTo(2014));
        assertThat(lastDay.getMonthOfYear(), equalTo(2));
        assertThat(lastDay.getDayOfMonth(), equalTo(28));
    }
}