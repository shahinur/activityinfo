package org.activityinfo.model.resource;

import org.junit.Test;

public class Base62Test {

    @Test
    public void testLengthEncoding() {
        long[] values = new long[] { 3713, 3782, 0, 1, 30, 59, 60, 61, 1000, 3713, 3714, 3780, 3781, 3782, 3783 };
        for(int i=0;i<values.length;++i) {
            assertEncodesCorrectly(values[i]);
        }

        for(int i=0;i<10;i++) {
            long value = (62L*31L*62L*31L-2L) + i;
            assertEncodesCorrectly(value);
        }
    }

    private void assertEncodesCorrectly(long value) {

        String encoded = Base62.encode(value);
        long decoded = Base62.decode(encoded, 0);

        System.out.println(value + " => " + encoded + " => " + decoded);

        if(decoded != value) {
            throw new AssertionError(value + " => " + encoded + " => " + decoded);
        }
    }


}