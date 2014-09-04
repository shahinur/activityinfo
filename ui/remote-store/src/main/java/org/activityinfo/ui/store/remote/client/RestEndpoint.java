package org.activityinfo.ui.store.remote.client;

import com.google.common.base.Function;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.*;
import org.activityinfo.promise.Promise;

public class RestEndpoint {
    public static final String ACCEPT = "Accept";
    public static final String CONTENT_TYPE = "Content-Type";

    public static final String APPLICATION_JSON = "application/json";

    private String uri;

    public RestEndpoint(String uri) {
        this.uri = uri;
    }

    public RestEndpoint resolve(String path) {
        return new RestEndpoint(uri + "/" + path);
    }

    private boolean isSuccessful(int statusCode) {
        return statusCode < 400;
    }

    /**
     * GETs the entity at this endpoint as JSON
     *
     * @return a Promise to the server's response
     */
    public Promise<Response> getJson() {
        RequestBuilder request = new RequestBuilder(RequestBuilder.GET, uri);
        request.setHeader(ACCEPT, APPLICATION_JSON);
        return send(request);
    }

    /**
     * GETs the entity at this endpoint as JSON
     *
     * @return a Promise to the server's response
     */
    public Promise<Response> postJson(String body) {
        return sendJson(RequestBuilder.POST, body);
    }

    public Promise<Response> putJson(String body) {
        return sendJson(RequestBuilder.PUT, body);
    }

    /**
     * Posts the given body as JSON to this endpoint.
     * @param body the entity encoded as JSON
     * @return a Promise to the Response
     */
    private Promise<Response> sendJson(RequestBuilder.Method method, String body) {

        assert method == RequestBuilder.POST || method == RequestBuilder.PUT;

        RequestBuilder request = new RequestBuilder(method, uri);
        request.setHeader(ACCEPT, APPLICATION_JSON);
        request.setHeader(CONTENT_TYPE, APPLICATION_JSON);
        request.setRequestData(body);
        return send(request);
    }

    public <T>  Promise<T> get(final Function<Response, T> reader) {
        return getJson().then(new Function<Response, T>() {
            @Override
            public T apply(Response response) {
                return reader.apply(response);
            }
        });
    }

    private Promise<Response> send(final RequestBuilder reqBuilder) {
        final Promise<Response> promise = new Promise<>();
        reqBuilder.setCallback(new RequestCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {
                GWT.log("Response " + response.getStatusCode() + " from " + reqBuilder.getUrl());

                if (isSuccessful(response.getStatusCode())) {
                    promise.resolve(response);
                } else {
                    GWT.log("Response message: " + response.getText());
                    promise.reject(new StatusCodeException(response.getStatusCode()));
                }
            }

            @Override
            public void onError(Request request, Throwable exception) {
                GWT.log("Error from " + reqBuilder.getUrl(), exception);
                promise.reject(exception);
            }
        });
        GWT.log("Sending request " + reqBuilder.getUrl());
        try {
            reqBuilder.send();
        } catch (RequestException e) {
            GWT.log("Request sending failed", e);
            promise.reject(e);
        }
        return promise;
    }
}
