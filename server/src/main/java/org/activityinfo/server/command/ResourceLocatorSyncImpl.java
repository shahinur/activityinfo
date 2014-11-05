package org.activityinfo.server.command;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import org.activityinfo.legacy.client.Dispatcher;
import org.activityinfo.legacy.client.remote.AbstractDispatcher;
import org.activityinfo.legacy.shared.adapter.ResourceLocatorAdaptor;
import org.activityinfo.legacy.shared.command.Command;
import org.activityinfo.legacy.shared.command.result.CommandResult;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.promise.Promise;

import java.util.logging.Level;
import java.util.logging.Logger;


public class ResourceLocatorSyncImpl implements ResourceLocatorSync {

    private static final Logger LOGGER = Logger.getLogger(ResourceLocatorSyncImpl.class.getName());

    private final DispatcherSync dispatcherSync;
    private final Dispatcher dispatcherAsync;
    private final ResourceLocatorAdaptor locatorAsync;

    @Inject
    public ResourceLocatorSyncImpl(DispatcherSync dispatcher) {
        this.dispatcherSync = dispatcher;
        this.dispatcherAsync = new AsyncDispatchAdapter();
        this.locatorAsync = new ResourceLocatorAdaptor(dispatcherAsync);
    }

    public Resource get(ResourceId resourceId) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void persist(FormInstance formInstance) {
        Promise<Void> result = locatorAsync.persist(formInstance);
        if(!result.isSettled()) {
            throw new IllegalStateException("Promise is not resolved");
        }
        if(result.getState() == Promise.State.REJECTED) {
            LOGGER.log(Level.SEVERE, "Failed to persist " + formInstance, result.getException());
            throw new RuntimeException(result.getException());
        }
    }

    public Resource get(AuthenticatedUser user, ResourceId resourceId) {
        throw new UnsupportedOperationException();
    }

    private class AsyncDispatchAdapter extends AbstractDispatcher {

        @Override
        public <T extends CommandResult> void execute(Command<T> command, AsyncCallback<T> callback) {
            T result;
            try {
                result = dispatcherSync.execute(command);
            } catch(Exception e) {
                callback.onFailure(e);
                return;
            }
            callback.onSuccess(result);
        }
    }
}
