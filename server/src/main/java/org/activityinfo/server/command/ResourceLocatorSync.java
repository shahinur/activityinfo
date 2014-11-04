package org.activityinfo.server.command;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import org.activityinfo.legacy.client.Dispatcher;
import org.activityinfo.legacy.client.remote.AbstractDispatcher;
import org.activityinfo.legacy.shared.command.Command;
import org.activityinfo.legacy.shared.command.result.CommandResult;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;


public class ResourceLocatorSync {

    private final DispatcherSync dispatcherSync;
    private final Dispatcher dispatcherAsync;

    @Inject
    public ResourceLocatorSync(DispatcherSync dispatcher) {
        this.dispatcherSync = dispatcher;
        this.dispatcherAsync = new AsyncDispatchAdapter();
    }

    public Resource get(ResourceId resourceId) {
        throw new UnsupportedOperationException();

    }

    public void persist(FormInstance formInstance) {
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
