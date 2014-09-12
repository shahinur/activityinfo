package org.activityinfo.model.table;

import com.google.common.collect.Maps;

import java.util.Map;

public class Bucket {
    private Map<Integer, Double> measureValues = Maps.newHashMap();
    private String[] dimensionValues;

    public Bucket(String[] dimensionValues) {
        this.dimensionValues = dimensionValues;
    }

    public void setMeasure(int measureIndex, double value) {
        measureValues.put(measureIndex, value);
    }

    public String getDimension(int i) {
        return dimensionValues[i];
    }

    public double getMeasure(int i) {
        if(measureValues.containsKey(i)) {
            return measureValues.get(i);
        } else {
            return Double.NaN;
        }
    }
}
