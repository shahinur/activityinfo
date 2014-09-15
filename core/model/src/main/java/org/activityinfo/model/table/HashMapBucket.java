package org.activityinfo.model.table;

import com.google.common.collect.Maps;

import java.util.Map;

public class HashMapBucket implements Bucket {
    private double value;
    private Map<String, String> dimensionValues = Maps.newHashMap();

    public HashMapBucket(String measureId, double value) {
        dimensionValues.put("_measure", measureId);
        this.value = value;
    }

    public String getDimensionValue(String dimensionId) {
        return dimensionValues.get(dimensionId);
    }

    public double getValue() {
        return value;
    }

    public void setDimensionValue(String dimensionId, String value) {
        if(value != null) {
            this.dimensionValues.put(dimensionId, value);
        }
    }
}
