package org.activityinfo.service.cubes;

import com.google.common.base.Supplier;

import java.util.*;

public class StockAggregator {

    public static final Supplier<StockAggregator> SUPPLIER = new Supplier<StockAggregator>() {
        @Override
        public StockAggregator get() {
            return new StockAggregator();
        }
    };

    private SortedMap<Date, Double> measurements = new TreeMap<>();

    private String[] dimValues;

    public void insert(Date measurementDate, double value, String[] dimValues) {
        measurements.put(measurementDate, value);
        if(this.dimValues == null) {
            this.dimValues = Arrays.copyOf(dimValues, dimValues.length);
        } else {
            if(!Arrays.equals(this.dimValues, dimValues)) {
                throw new IllegalStateException("primary key / dimensions mismatch");
            }
        }
    }

    @Override
    public String toString() {
        String reports = Arrays.toString(dimValues);
        for (Map.Entry<Date, Double> entry : measurements.entrySet()) {
            reports += ", " + entry.getKey().getMonth() + " = " + entry.getValue();
        }
        return reports;
    }

    public String[] getDimKey() {
        return dimValues;
    }

    public double getAverage() {
        double sum = 0;
        double count = measurements.size();
        for(Double value : measurements.values()) {
            sum += value;
        }
        return sum / count;
    }
}
