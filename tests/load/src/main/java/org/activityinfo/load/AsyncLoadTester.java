package org.activityinfo.load;

import com.google.common.base.Stopwatch;
import com.google.common.base.Supplier;
import com.sun.jersey.api.client.ClientResponse;
import org.joda.time.Duration;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class AsyncLoadTester {
    private final Supplier<Future<ClientResponse>> requester;
    private Duration duration;
    private GrowthFunction growthFunction = null;

    public AsyncLoadTester(Supplier<Future<ClientResponse>> requester) {
        this.requester = requester;
    }

    public Supplier<Future<ClientResponse>> getRequester() {
        return requester;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public GrowthFunction getGrowthFunction() {
        return growthFunction;
    }

    public void setGrowthFunction(GrowthFunction growthFunction) {
        this.growthFunction = growthFunction;
    }

    public RequestStats run() throws InterruptedException {
        RequestTracker tracker = new RequestTracker();

        // During the test period, make sure we have at least
        // numConcurrentRequests active at any time
        Stopwatch stopwatch = Stopwatch.createStarted();
        while(stopwatch.elapsed(TimeUnit.MILLISECONDS) < duration.getMillis()) {
            int numPending = tracker.countPending();
            int targetNum = growthFunction.getValue();
            while(numPending < targetNum) {
              //  System.out.println("Submitting new request...");
                tracker.add(requester);
                numPending++;
            }
            Thread.sleep(10);
        }
        System.out.println("Test period complete, waiting for remaining requests to finish...");

        tracker.waitAll();

        return tracker.getRequestStats();
    }
}
