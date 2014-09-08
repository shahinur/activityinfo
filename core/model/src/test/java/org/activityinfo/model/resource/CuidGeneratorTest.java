package org.activityinfo.model.resource;

import org.junit.Test;

public class CuidGeneratorTest {


    @Test
    public void test() {

        long time = System.currentTimeMillis();

        long fiftyYears = 50L * 365L * 24L * 60L * 60L * 1000L;

        System.out.println("Milliseconds in 10 years = " + fiftyYears);
        System.out.println("chars = " + Math.log(fiftyYears) / Math.log(62));


        System.out.println();
    }

}