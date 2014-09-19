package org.activityinfo.service.cubes;

import com.google.common.base.Supplier;
import com.google.common.collect.Multimap;

import java.util.*;

public class StockAggregator {

    public static final Supplier<StockAggregator> SUPPLIER = new Supplier<StockAggregator>() {
        @Override
        public StockAggregator get() {
            return new StockAggregator();
        }
    };

    private SortedMap<Date, Double> measurements = new TreeMap<>();

    private Multimap<Integer, String> dimValues;

    public void insert(Date measurementDate, double value, Multimap<Integer, String> dimValues) {
        measurements.put(measurementDate, value);
        if(this.dimValues == null) {
            this.dimValues = dimValues;
        } else {
            if(!this.dimValues.equals(dimValues)) {
                System.out.println(dimValues + " vs " + this.dimValues);
                throw new IllegalStateException("primary key / dimensions mismatch");
            }
        }
    }

    @Override
    public String toString() {
        String reports = dimValues.toString();
        for (Map.Entry<Date, Double> entry : measurements.entrySet()) {
            reports += ", " + entry.getKey().getMonth() + " = " + entry.getValue();
        }
        return reports;
    }

    public Multimap<Integer, String> getDimKey() {
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
