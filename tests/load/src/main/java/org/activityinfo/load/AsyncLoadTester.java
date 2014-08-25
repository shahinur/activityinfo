package org.activityinfo.load;

import com.google.common.base.Stopwatch;
import com.google.common.base.Supplier;
import com.sun.jersey.api.client.ClientResponse;

import java.util.concurrent.Future;

public class AsyncLoadTester {

    private final Supplier<Future<ClientResponse>> requester;
    private final LoadProfile loadProfile;

    public AsyncLoadTester(Supplier<Future<ClientResponse>> requester, LoadProfile loadProfile) {
        this.requester = requester;
        this.loadProfile = loadProfile;
    }

    /**
     * Starts the test run
     * @return statistics on the requests
     *
     * @throws InterruptedException
     */
    public RequestStats run() throws InterruptedException {

        Stopwatch stopwatch = Stopwatch.createStarted();
        RequestTracker tracker = new RequestTracker();

        while(!loadProfile.isFinished(stopwatch, tracker.totalRequestCount())) {
            int numPending = tracker.countPending();
            int maxConcurrent = loadProfile.getMaxConcurrentRequests(stopwatch);
            while(numPending < maxConcurrent) {
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
