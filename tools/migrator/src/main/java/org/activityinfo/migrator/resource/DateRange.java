package org.activityinfo.migrator.resource;

import org.activityinfo.model.type.time.LocalDateType;
import org.activityinfo.model.resource.Record;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class DateRange {

    public static Record startDate(ResultSet rs) throws SQLException {
        Date date = rs.getDate("date1");
        if(rs.wasNull()) {
            return null;
        } else {
            return LocalDateType.fromDate(date);
        }
    }

    public static Record endDate(ResultSet rs) throws SQLException {
        Date date = rs.getDate("date2");
        if(rs.wasNull()) {
            return null;
        } else {
            return LocalDateType.fromDate(date);
        }
    }
}
