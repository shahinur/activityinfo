package org.activityinfo.model.type.number;

import com.google.common.base.Strings;
import org.activityinfo.model.record.IsRecord;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.record.Records;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.enumerated.EnumFieldValue;
import org.activityinfo.model.type.enumerated.EnumType;

public class Quantity implements FieldValue, IsRecord {

    public static final String UNKNOWN_UNITS = "unknown";

    private final double value;
    private String units;
    private AggregationType aggregationType = AggregationType.SUM;

    public Quantity(double value) {
        this(value, UNKNOWN_UNITS);
    }

    public Quantity(double value, String units) {
        this.value = value;
        this.units = Strings.emptyToNull(units);
    }

    public double getValue() {
        return value;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public AggregationType getAggregationType() {
        return aggregationType;
    }

    public void setAggregationType(AggregationType aggregationType) {
        this.aggregationType = aggregationType;
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return QuantityType.TYPE_CLASS;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Quantity quantity = (Quantity) o;

        if (Double.compare(quantity.value, value) != 0) {
            return false;
        }

        if (units == null) {
            return quantity.units == null;
        } else {
            return units.equals(quantity.units);
        }
    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(value);
        return ((int) (temp ^ (temp >>> 32))) ^ (units != null ? units.hashCode() : 0);
    }

    public static Quantity fromRecord(Record record) {
        Quantity quantityType = new Quantity(record.getDouble("value"), record.isString("units"));
        quantityType.setAggregationType(EnumType.read(record, "aggregation", AggregationType.class, AggregationType.SUM));
        return quantityType;
    }

    @Override
    public Record asRecord() {
        return Records.builder()
            .set(TYPE_CLASS_FIELD_NAME, getTypeClass().getId())
            .set("value", value)
            .set("units", units)
            .set("aggregation", new EnumFieldValue(aggregationType.name()))
            .build();
    }

    public boolean hasUnits() {
        return units != null;
    }

    @Override
    public String toString() {
        return "Quantity{" +
               "value=" + value +
               ", units='" + units + '\'' +
               '}';
    }
}
