package org.activityinfo.service.cubes;

public interface Aggregator {

    public void update(double value);

    public double getResult();

}
