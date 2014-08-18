package org.activityinfo.server.endpoint.odk;

import org.activityinfo.legacy.shared.command.CreateSite;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.store.ResourceStore;

import java.util.Iterator;

/**
 * Created by alex on 11/5/14.
 */
public class TestResourceStore implements ResourceStore {
    public TestResourceStore load(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Resource> openCursor(ResourceId formClassId) {
        throw new UnsupportedOperationException();

    }

    @Override
    public Resource get(ResourceId resourceId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void createResource(ResourceId resourceId, Resource resource) {
        throw new UnsupportedOperationException();

    }

    @Override
    public Resource get(AuthenticatedUser user, ResourceId resourceId) {
        return null;
    }

    @Override
    public void put(AuthenticatedUser user, Resource resource) {

    }

    public Resource getLastUpdated() {
        throw new UnsupportedOperationException();

    }
}
