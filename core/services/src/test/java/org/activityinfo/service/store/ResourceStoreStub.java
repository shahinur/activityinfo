package org.activityinfo.service.store;

import com.google.common.base.Preconditions;
import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.service.auth.AuthenticatedUser;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class ResourceStoreStub implements ResourceStore {

    public static final ResourceId MY_RESOURCE_ID = ResourceId.valueOf("myid");

    public static final ResourceId NEW_RESOURCE_ID_TO_COMMIT = ResourceId.valueOf("newid1");
    public static final int NEW_VERSION = 21;

    @Override
    public Resource get(@InjectParam AuthenticatedUser user, ResourceId resourceId) {

        if(!resourceId.equals(MY_RESOURCE_ID)) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return getMyResource();
    }

    public static Resource getMyResource() {
        Resource resource = Resources.createResource();
        resource.setId(MY_RESOURCE_ID);
        resource.setOwnerId(ResourceId.ROOT_ID);
        resource.setVersion(15);
        resource.set("stringProperty", "stringValue");
        resource.set("booleanValue", true);
        resource.set("recordProperty", new Record()
                    .set("subStringProp", "A")
                    .set("anotherSubProp", "B")
                    .set("subBoolProp", true));
        return resource;
    }

    public static Resource getNewResourceThatWillCommit() {
        Resource resource = Resources.createResource();
        resource.setId(NEW_RESOURCE_ID_TO_COMMIT);
        resource.setOwnerId(ResourceId.valueOf("parent32"));
        resource.set("stringProperty", "_string1__");
        resource.set("falseValue", false);
        resource.set("recordProperty", new Record()
                .set("recordId", "record1")
                .set("subRecord", new Record()
                    .set("subSubString", "downUnder")));
        return resource;
    }

    @Override
    public Set<Resource> get(@InjectParam AuthenticatedUser user, Set<ResourceId> resourceIds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResourceTree queryTree(@InjectParam AuthenticatedUser user, ResourceTreeRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TableData queryTable(@InjectParam AuthenticatedUser user, TableModel tableModel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UpdateResult put(AuthenticatedUser user, Resource resource) {
        return put(user, resource.getId(), resource);
    }

    @Override
    public UpdateResult put(@InjectParam AuthenticatedUser user, ResourceId resourceId, Resource resource) {
        Preconditions.checkNotNull(resourceId, "resourceId");
        Preconditions.checkNotNull(resource, "resource");

        if(NEW_RESOURCE_ID_TO_COMMIT.equals(resource.getId())) {

            assertThat(resource, equalTo(getNewResourceThatWillCommit()));

            return UpdateResult.committed(resource.getId(), NEW_VERSION);
        }

        throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }


    @Override
    public List<ResourceNode> getUserRootResources(@InjectParam AuthenticatedUser user) {
        throw new UnsupportedOperationException();
    }
}
