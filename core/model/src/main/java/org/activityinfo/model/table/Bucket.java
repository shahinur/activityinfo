package org.activityinfo.model.table;

public interface Bucket {

    double getValue();

    String getDimensionValue(String measureId);

}
