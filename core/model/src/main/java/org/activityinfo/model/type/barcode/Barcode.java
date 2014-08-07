package org.activityinfo.model.type.barcode;

import org.activityinfo.model.resource.IsRecord;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.FieldValue;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class Barcode implements FieldValue, IsRecord {

    public static final String UNKNOWN = "unknown";

    private final String value;
    private String units;

    public Barcode(String value) {
        this(value, UNKNOWN);
    }

    public Barcode(String value, String units) {
        this.value = value;
        this.units = units;
    }

    public String getValue() {
        return value;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return FieldTypeClass.BARCODE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Barcode barcode = (Barcode) o;

        if (!StringUtils.equals(barcode.value, value)) {
            return false;
        }

        if (units == null) {
            return barcode.units == null;
        } else {
            return units.equals(barcode.units);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.value);
    }

    public static FieldValue fromRecord(Record record) {
        return new Barcode(record.getString("value"), record.isString("units"));
    }

    @Override
    public Record asRecord() {
        return new Record()
                .set(TYPE_CLASS_FIELD_NAME, getTypeClass().getId())
                .set("value", value)
                .set("units", units);
    }
}
