package org.activityinfo.load;

import org.joda.time.Period;
import org.junit.Test;

public class GrowthFunctionTest {

    @Test
    public void testGetValue() throws Exception {

        LogisticGrowthFunction f = LogisticGrowthFunction.rampUpTo(10).during(Period.seconds(30));

        for(double x = 0; x < 1d; x+=0.10) {
            System.out.println(f.apply(x));
        }

    }
}