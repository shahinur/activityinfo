package org.activityinfo.service.cubes;

import com.google.common.base.Supplier;

class SumAggregator implements Aggregator {

    public static final Supplier<SumAggregator> SUPPLIER = new Supplier<SumAggregator>() {
        @Override
        public SumAggregator get() {
            return new SumAggregator();
        }
    };

    double sum = 0;

    @Override
    public void update(double value) {
        sum += value;
    }

    @Override
    public double getResult() {
        return sum;
    }
}
