package org.activityinfo.server.store;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.legacy.client.remote.AbstractDispatcher;
import org.activityinfo.legacy.shared.adapter.ResourceLocatorAdaptor;
import org.activityinfo.legacy.shared.command.Command;
import org.activityinfo.legacy.shared.command.result.CommandResult;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.promise.Promise;
import org.activityinfo.server.command.DispatcherSync;
import org.activityinfo.service.ResourceLocatorSync;

public class ResourceStoreModule extends AbstractModule {
    @Override
    protected void configure() {

    }

    @Singleton
    @Provides
    public ResourceLocatorSync provideResourceLocator(DispatcherSync dispatcherSync) {
        // we have to do a few contortions to get what we need using
        // the existing adapater scaffolding until we're ready to rip of the
        // bandaids
        DispatcherAsyncWrapper dispatcherAsync = new DispatcherAsyncWrapper(dispatcherSync);
        ResourceLocatorAdaptor adaptor = new ResourceLocatorAdaptor(dispatcherAsync);
        ResourceLocatorSyncWrapper locatorSync = new ResourceLocatorSyncWrapper(adaptor);

        return locatorSync;
    }

    private class DispatcherAsyncWrapper extends AbstractDispatcher {
        private final DispatcherSync dispatcher;

        private DispatcherAsyncWrapper(DispatcherSync dispatcher) {
            this.dispatcher = dispatcher;
        }

        @Override
        public <T extends CommandResult> void execute(Command<T> command, AsyncCallback<T> callback) {
            T result;
            try {
                result = dispatcher.execute(command);
            } catch(Exception e) {
                callback.onFailure(e);
                return;
            }
            callback.onSuccess(result);
        }
    }

    private class ResourceLocatorSyncWrapper implements ResourceLocatorSync {
        private final ResourceLocator locatorAsync;

        private ResourceLocatorSyncWrapper(ResourceLocator locatorAsync) {
            this.locatorAsync = locatorAsync;
        }

        @Override
        public Resource getResource(ResourceId resourceId) {

            Promise<FormClass> formClass = locatorAsync.getFormClass(resourceId);
            formClass.then(new AsyncCallback<FormClass>() {
                @Override
                public void onFailure(Throwable caught) {
                    throw new RuntimeException(caught);
                }

                @Override
                public void onSuccess(FormClass result) {
                }
            });
            return formClass.get().asResource();
        }
    }
}
