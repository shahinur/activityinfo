package org.activityinfo.ui.app.client.store;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.UserResource;
import org.activityinfo.model.system.ApplicationClassProvider;
import org.activityinfo.service.store.CommitStatus;
import org.activityinfo.service.store.UpdateResult;
import org.activityinfo.ui.app.client.action.RemoteUpdateHandler;
import org.activityinfo.ui.app.client.request.RemoveRequest;
import org.activityinfo.ui.app.client.request.Request;
import org.activityinfo.ui.app.client.request.SaveRequest;
import org.activityinfo.ui.flux.dispatcher.Dispatcher;
import org.activityinfo.ui.flux.store.AbstractStore;
import org.activityinfo.ui.flux.store.Status;

import java.util.Map;

/**
 * The ResourceCache manages the caching and retrieval of individual
 * resource objects.
 *
 */
public class ResourceStore extends AbstractStore implements RemoteUpdateHandler {

    private ApplicationClassProvider classProvider;
    private Map<ResourceId, Status<UserResource>> resources = Maps.newHashMap();


    public ResourceStore(Dispatcher dispatcher) {
        super(dispatcher);
        classProvider = new ApplicationClassProvider();
    }


    @Override
    public void requestStarted(Request request) {

    }

    @Override
    public void requestFailed(Request request, Throwable e) {

    }

    @Override
    public <R> void processUpdate(Request<R> request, R response) {
        if(response instanceof UserResource) {
            cache((UserResource)response);
        } else if (request instanceof SaveRequest) {
            SaveRequest saveRequest = (SaveRequest) request;
            Resource updatedResource = saveRequest.getUpdatedResource();
            cache(UserResource.userResource(updatedResource).setEditAllowed(true));
        } else if (request instanceof RemoveRequest) {
            UpdateResult updatedResult = (UpdateResult) response;
            if (updatedResult.getStatus() == CommitStatus.COMMITTED) {
                boolean removed = resources.remove(updatedResult.getResourceId()) != null;
                if (removed) {
                    fireChange();
                }
            }
        }
    }

    private void cache(UserResource response) {
        resources.put(response.getResourceId(), Status.cache(response.copy()));
        fireChange();
    }

    public Status<UserResource> get(ResourceId id) {
        Status<UserResource> resource = resources.get(id);
        if(resource == null) {
            return Status.unavailable();
        } else {
            return resource;
        }
    }

    public Status<FormClass> getFormClass(ResourceId id) {

        if(classProvider.isApplicationFormClass(id)) {
            return Status.cache(classProvider.get(id));
        }

        return get(id).join(new Function<UserResource, FormClass>() {
            @Override
            public FormClass apply(UserResource input) {
                return FormClass.fromResource(input.getResource());
            }
        });
    }
}
