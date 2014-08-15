package org.activityinfo.ui.store.remote.client;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.common.base.Function;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.*;
import org.activityinfo.promise.Promise;

import javax.annotation.Nullable;

public class RestEndpoint {
    private String uri;

    public RestEndpoint(String uri) {
        this.uri = uri;
    }

    public RestEndpoint resolve(String path) {
        return new RestEndpoint(uri + "/" + path);
    }

    public Promise<String> get() {

        final Promise<String> result = new Promise<>();
        RequestBuilder request = new RequestBuilder(RequestBuilder.GET, uri);
        request.setHeader("Accept", "application/json");
        request.setCallback(new RequestCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {
                if(response.getStatusCode() != 200) {
                    result.onFailure(new RuntimeException("Status code: " + response.getStatusCode()));
                } else {
                    result.onSuccess(response.getText());
                }
            }

            @Override
            public void onError(Request request, Throwable exception) {
                result.onFailure(exception);
            }
        });
        try {
            request.send();
        } catch (RequestException e) {
            result.reject(e);
        }

        return result;
    }

    public Promise<String> post(String json) {

        final Promise<String> result = new Promise<>();
        RequestBuilder request = new RequestBuilder(RequestBuilder.POST, uri);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-Type", "application/json");

        try {
            request.sendRequest(json, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    if(response.getStatusCode() < 200 || response.getStatusCode() >= 300) {
                        result.onFailure(new RuntimeException("Status code: " + response.getStatusCode()));
                    } else {
                        result.onSuccess(response.getText());
                    }
                }
                @Override
                public void onError(Request request, Throwable exception) {
                    result.onFailure(exception);
                }
            });
            request.send();
        } catch (RequestException e) {
            result.reject(e);
        }

        return result;
    }

    public <T> Promise<T> post(final String json, final ObjectMapper<T> objectMapper) {
        return post(json).then(new Function<String, T>() {
            @Nullable
            @Override
            public T apply(@Nullable String input) {
                return objectMapper.read(json);
            }
        });
    }

    public <T>  Promise<T> get(final ObjectMapper<T> reader) {
        return get().then(new Function<String, T>() {
            @Nullable
            @Override
            public T apply(@Nullable String input) {
                GWT.log("JSON = " + input);
                return reader.read(input);
            }
        });
    }
}
