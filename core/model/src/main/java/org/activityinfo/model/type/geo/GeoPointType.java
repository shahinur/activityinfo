package org.activityinfo.model.type.geo;

import com.bedatadriven.rebar.time.calendar.LocalDate;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.RecordFieldTypeClass;
import org.activityinfo.model.type.component.ComponentReader;
import org.activityinfo.model.type.component.NullComponentReader;

/**
 * A value type describing a point within the WGS84 Geographic Reference System.
 */
public class GeoPointType implements FieldType {

    public static final String TYPE_ID = "GEOGRAPHIC_POINT";

    public static final GeoPointType INSTANCE = new GeoPointType();

    public static final FieldTypeClass TYPE_CLASS = new RecordFieldTypeClass() {
        @Override
        public String getId() {
            return TYPE_ID;
        }

        @Override
        public String getLabel() {
            return "Latitude/Longitude";
        }

        @Override
        public FieldType createType() {
            return INSTANCE;
        }

        @Override
        public FieldValue deserialize(Record record) {
            return GeoPoint.fromRecord(record);
        }
    };

    private GeoPointType() {  }

    @Override
    public FieldTypeClass getTypeClass() {
        return TYPE_CLASS;
    }

    @Override
    public ComponentReader<String> getStringReader(String fieldName, String componentId) {
        return new NullComponentReader<>();
    }

    @Override
    public ComponentReader<LocalDate> getDateReader(String name, String componentId) {
        return new NullComponentReader<>();
    }

}
