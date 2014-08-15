package org.activityinfo.legacy.shared.adapter;

import com.google.gwt.http.client.RequestBuilder;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.promise.Promise;

/**
 * Retrieves resources from the server, caching to local storage
 *
 */
public class ResourceFetcher {

    public ResourceFetcher() {

    }

    public Promise<Resource> getResource(ResourceId resourceId) {
        return fetchResource(resourceId);
    }

    private Promise<Resource> fetchResource(ResourceId resourceId) {
        RequestBuilder request = new RequestBuilder(RequestBuilder.GET, "/service/resource/");
        throw new UnsupportedOperationException();
    }

}
