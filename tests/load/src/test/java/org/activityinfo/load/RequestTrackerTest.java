package org.activityinfo.load;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class RequestTrackerTest {

    @Test
    public void testMedian() throws Exception {
        assertThat(RequestTracker.median(new double[] { 3, 2, 1}), equalTo(2d));
        assertThat(RequestTracker.median(new double[] { 1, 3 }), equalTo(2d));
        assertThat(RequestTracker.median(new double[] { 1, 4, 4, 4, 3 }), equalTo(4d));
        assertThat(RequestTracker.median(new double[] { 5 }), equalTo(5d));
    }
}