package org.activityinfo.migrator.resource;

import org.activityinfo.model.resource.Record;
import org.activityinfo.model.type.geo.GeoPointType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Geometry {

    public static Record from(ResultSet rs) throws SQLException {
        boolean wasNull = false;
        double x1 = rs.getDouble("x1");
        wasNull = wasNull || rs.wasNull();
        double y1 = rs.getDouble("y1");
        wasNull = wasNull || rs.wasNull();
        double x2 = rs.getDouble("x2");
        wasNull = wasNull || rs.wasNull();
        double y2 = rs.getDouble("y2");
        wasNull = wasNull || rs.wasNull();

        if(wasNull) {
            return null;
        } else {
            Record extents = new Record();
            extents.set("x1", x1);
            extents.set("y1", y1);
            extents.set("x2", x2);
            extents.set("y2", y2);
            return extents;
        }
    }

    public static Record point(ResultSet rs) throws SQLException {
        double x = rs.getDouble("x");
        boolean wasNull = rs.wasNull();
        double y = rs.getDouble("y");
        wasNull = wasNull || rs.wasNull();

        if(wasNull) {
            return null;
        } else {
            return GeoPointType.fromXY(x, y);
        }
    }
}
