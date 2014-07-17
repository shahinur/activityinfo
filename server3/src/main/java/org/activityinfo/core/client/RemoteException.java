package org.activityinfo.core.client;

/**
 * A collection of "expected problems" that the UI should gracefully handle.
 */
public class RemoteException extends RuntimeException {

    public RemoteException() {
    }

    public RemoteException(String message) {
        super(message);
    }
}
