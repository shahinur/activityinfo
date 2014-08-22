package org.activityinfo.load;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.util.concurrent.Futures;
import com.sun.jersey.api.client.ClientResponse;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.concurrent.Future;

public class Request {

    public enum Outcome {
        SUCCESS,
        TIMEOUT,
        ERROR
    }

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

    public boolean isTimedOut() {
        return timedOut;
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

    public Outcome getOutcome() {
        if(timedOut) {
            return Outcome.TIMEOUT;
        } else if(statusCode < 300) {
            return Outcome.SUCCESS;
        } else {
            return Outcome.ERROR;
        }
    }

    public RequestError getRequestError() {
        if(getOutcome() != Outcome.ERROR) {
            throw new UnsupportedOperationException("Request outcome was " + getOutcome());
        }
        try {
            return new RequestError(statusCode, getErrorMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getErrorMessage() throws IOException {
        ClientResponse clientResponse = Futures.getUnchecked(response);
        if(clientResponse.getHeaders().getFirst("Content-Type").equals("plain/text")) {
            return clientResponse.getEntity(String.class);
        } else {
            byte[] entity = ByteStreams.toByteArray(clientResponse.getEntityInputStream());
            if(entity.length == 0) {
                return "<empty body>";
            } else {
                return new String(entity, 0, Math.min(25, entity.length), Charsets.UTF_8);
            }
        }
    }
}
