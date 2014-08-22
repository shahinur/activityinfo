package org.activityinfo.load;

import com.google.common.base.Functions;
import org.joda.time.Period;

public class LoadProfiles {

    public static LoadProfile once() {
        return new LoadProfile()
                .setMaxConcurrentRequests(Functions.constant(1))
                .setMaxRequestCount(1);
    }

    public static LoadProfile rush10() {
        return new LoadProfile()
                .setTestDuration(Period.minutes(5))
                .setMaxConcurrentRequests(
                        LogisticGrowthFunction.rampUpTo(10).during(Period.seconds(60)));
    }
}
