package org.activityinfo.load;

import com.google.common.util.concurrent.Futures;
import com.sun.jersey.api.client.ClientResponse;
import org.joda.time.DateTime;

import java.util.concurrent.Future;

public class Request {

    private Future<ClientResponse> response;
    private DateTime startTime = new DateTime();
    private DateTime completeTime;
    private boolean timedOut = false;
    private int statusCode;

    private long synchronousDuration = 0;

    public Request(DateTime startTime, Future<ClientResponse> response) {
        this.response = response;
        this.startTime = startTime;
        this.synchronousDuration = System.currentTimeMillis() - startTime.getMillis();
    }

    public boolean isDone() {
        return response.isDone();
    }

    public boolean isSuccess() {
        return statusCode == 201;
    }

    public int getStatus() {
        return statusCode;
    }

    public void onComplete() {
        if(completeTime != null) {
            throw new IllegalStateException();
        }
        completeTime = new DateTime();
        statusCode = Futures.getUnchecked(response).getStatus();
//
//        double propSynchronous = ((double)synchronousDuration) / getLatency() * 100;
//        System.out.println(String.format("Synchronous: %.2f%%", propSynchronous));

    }

    public void cancel() {
        response.cancel(true);
        timedOut = true;
    }

    public long millisPending() {
        return System.currentTimeMillis() - startTime.getMillis();
    }

    public double getLatency() {
        if(completeTime != null) {
            return completeTime.getMillis() - startTime.getMillis();
        } else {
            return Double.NaN;
        }
    }

    public DateTime getStartTime() {
        return startTime;
    }
}
