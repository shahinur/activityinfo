package org.activityinfo.promise;

import com.google.common.base.Function;

public class SqrtAsync implements Function<Double, Promise<Double>> {
    @Override
    public Promise<Double> apply(Double input) {
        return Promise.resolved(Math.sqrt(input));
    }
}
