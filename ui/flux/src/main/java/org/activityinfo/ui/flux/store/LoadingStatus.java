package org.activityinfo.ui.flux.store;

import org.activityinfo.promise.Promise;

public enum LoadingStatus {
    PENDING,
    LOADED,
    FAILED;

    public static LoadingStatus valueOf(Promise.State state) {
        switch(state) {
            case FULFILLED:
                return LOADED;
            case REJECTED:
                return FAILED;
            case PENDING:
                return LoadingStatus.PENDING;
        }
        throw new IllegalArgumentException();
    }
}
