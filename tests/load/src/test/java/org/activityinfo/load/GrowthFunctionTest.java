package org.activityinfo.load;

import org.joda.time.Duration;
import org.junit.Test;

public class GrowthFunctionTest {

    @Test
    public void testGetValue() throws Exception {

        GrowthFunction f = new GrowthFunction(Duration.standardMinutes(1), 10);

        for(double x = 0; x < 1d; x+=0.10) {
            System.out.println(f.getValue(x));
        }

    }
}