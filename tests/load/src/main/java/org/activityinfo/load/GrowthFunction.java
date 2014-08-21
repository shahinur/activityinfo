package org.activityinfo.load;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Stopwatch;
import org.joda.time.Duration;

import java.util.concurrent.TimeUnit;

/**
 * Function of time that uses a logistic growth function for a ramp up period
 * followed by a steady state
 */
public class GrowthFunction {

    private final int maxValue;
    private final double rampUpDuration;
    private final Stopwatch stopwatch = Stopwatch.createStarted();


    public GrowthFunction(Duration rampUpDuration, int maxValue) {
        this.rampUpDuration = rampUpDuration.getMillis();
        this.maxValue = maxValue;
    }

    public int getValue() {
        double elapsedMillis = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        if(elapsedMillis > rampUpDuration) {
            return maxValue;
        } else {
            double proportionElapsed = elapsedMillis / rampUpDuration;
            return getValue(proportionElapsed);
        }
    }

    @VisibleForTesting
    int getValue(double proportionElapsed) {

        // scale x into the range [-6, 6]
        double x = (proportionElapsed * 12d) - 6d;
        double y = 1d / (1d + Math.exp(-x));

        // scale y to our max value
        return (int)Math.round( y * ((double)maxValue));
    }
}
