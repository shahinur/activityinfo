package org.activityinfo.load;

import com.google.common.base.Stopwatch;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.sun.jersey.api.client.ClientResponse;
import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class RequestTracker {


    private long timeout = 30 * 1000;
    private DateTime start = new DateTime();

    private List<Request> pending = Lists.newLinkedList();
    private List<Request> completed = Lists.newArrayList();

    private Stopwatch updateTimer = Stopwatch.createUnstarted();

    private long synchronousExecutionMillis = 0;

    public Request add(Supplier<Future<ClientResponse>> requester) {

        if(!updateTimer.isRunning()) {
            updateTimer.start();
        }

        DateTime requestStart = new DateTime();
        Future<ClientResponse> response = requester.get();
        synchronousExecutionMillis += (System.currentTimeMillis() - requestStart.getMillis());
        Request request = new Request(requestStart, response);
        pending.add(request);
        return request;
    }


    public void updateStatus() {
        ListIterator<Request> it = pending.listIterator();
        while(it.hasNext()) {
            Request request = it.next();
            if(request.isDone()) {
                request.onComplete();
                completed.add(request);
                it.remove();
            } else if(request.millisPending() > timeout) {
                request.cancel();
                completed.add(request);
                it.remove();
            }
        }

        if(updateTimer.elapsed(TimeUnit.SECONDS) > 2) {
            printSummary();
            updateTimer.reset();
        }
    }

    public int countPending() {
        updateStatus();
        return pending.size();
    }

    public void waitAll() throws InterruptedException {
        while(countPending() > 0) {
            updateStatus();
            Thread.sleep(10);
        }
    }

    public void printSummary() {
        System.out.println(String.format(
            "Pending: %3d   Completed: %3d  Successful: %3d%%  Median Latency: %7.0f ms",
                pending.size(),
                completed.size(),
                pctSuccessful(),
                medianLatency()));
    }


    private double medianLatency() {
        if(completed.isEmpty()) {
            return 0;
        } else {

            // Calculate the medium latency over a rolling window of 5 seconds

            DateTime periodBegin = new DateTime().minusSeconds(5);
            double latency[] = new double[completed.size()];

            int i;
            int j = completed.size() - 1;
            for (i = 0; i != latency.length; ++i) {
                Request request = completed.get(j--);
                if(request.getStartTime().isBefore(periodBegin)) {
                    break;
                }
                latency[i] = request.getLatency();
            }
            return median(Arrays.copyOf(latency, i));
        }
    }


    static double median(double[] latency) {
        Arrays.sort(latency);

        int midPoint = (latency.length / 2);
        if(latency.length % 2 == 1) {
            return latency[midPoint];
        } else {
            return (latency[midPoint] + latency[midPoint-1]) / 2d;
        }
    }

    private long pctSuccessful() {
        double numSuccessful = 0;
        for(Request response : completed) {
            if(response.isSuccess()) {
                numSuccessful++;
            }
        }
        return Math.round(numSuccessful / completed.size() * 100d);
    }

    public List<Request> getRequests() {
        return completed;
    }

    public RequestStats getRequestStats() {
        return new RequestStats(start, completed);
    }
}
