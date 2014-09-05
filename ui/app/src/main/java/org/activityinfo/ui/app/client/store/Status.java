package org.activityinfo.ui.app.client.store;

import org.activityinfo.model.resource.FolderProjection;

public class Status<T> {

    private T value;
    private double cacheTime;
    private boolean loading;

    private Status() {
    }

    public Status(T value, double cacheTime) {
        this.value = value;
        this.cacheTime = cacheTime;
    }

    /**
     *
     * @return true if we have <em>something</em> available locally.
     */
    public boolean isAvailable() {
        return value != null;
    }

    public boolean isLoading() {
        return loading;
    }

    public double getAge() {
        return Double.MAX_VALUE;
    }

    public T get() {
        assert value != null;
        return value;
    }

    public static <T> Status<T> cache(T response) {
        return new Status<>(response, System.currentTimeMillis());
    }

    public Status<FolderProjection> withLoading(boolean loading) {
        Status status = new Status(value, cacheTime);
        status.loading = loading;
        return status;
    }

    public static <T> Status<T> unavailable() {
        Status unavailable = new Status();
        return unavailable;
    }

    public boolean requiresFetch() {
        return !isAvailable() && !isLoading();
    }
}
