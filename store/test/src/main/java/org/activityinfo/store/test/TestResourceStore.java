package org.activityinfo.store.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.io.Resources;
import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.client.LocatorAdapter;
import org.activityinfo.client.ResourceLocator;
import org.activityinfo.client.TestRemoteStoreService;
import org.activityinfo.model.analysis.PivotTableModel;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.json.ObjectMapperFactory;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.table.Bucket;
import org.activityinfo.service.store.*;
import org.activityinfo.store.hrd.HrdResourceStore;
import org.activityinfo.store.hrd.StoreContext;
import org.activityinfo.store.hrd.dao.WorkspaceCreation;

import javax.ws.rs.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;

@SuppressWarnings("GwtClientClassFromNonInheritedModule")
public class TestResourceStore implements ResourceStore {

    private HrdResourceStore store;

    private AuthenticatedUser currentUser = new AuthenticatedUser(1);

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
                    .setApplyAllHighRepJobPolicy());

    private Resource lastUpdated;
    private final StoreContext storeContext;

    public void setUp() {
        helper.setUp();
    }

    public void tearDown() {
//        helper.tearDown();
    }

    public TestResourceStore() {
        storeContext = new StoreContext();
        store = new HrdResourceStore(storeContext);
    }

    public ResourceStore unwrap() {
        return store;
    }

    /**
     * Loads a set of resources from a json resource on the classpath
     * @param resourceName the name of the class path resource to load
     * @return this {@code TestResourceStore}, for chaining
     * @throws IOException
     */
    public TestResourceStore load(String resourceName) throws IOException {
        ObjectMapper mapper = ObjectMapperFactory.get();
        URL resourceURL = Resources.getResource(resourceName);
        TestWorkspace[] workspaces = mapper.readValue(resourceURL, TestWorkspace[].class);

        for(int i=0;i!=workspaces.length;++i) {
            TestWorkspace workspace = workspaces[i];
            AuthenticatedUser user = new AuthenticatedUser(workspace.getUserId());
            WorkspaceCreation workspaceCreation = new WorkspaceCreation(storeContext, user);
            workspaceCreation.createWorkspace(workspace.getWorkspace(), workspace.getResources());
        }
        return this;
    }


    public static ResourceLocator createLocator(String resourceName) {
        try {
            return new LocatorAdapter(new TestRemoteStoreService(new TestResourceStore().load(resourceName)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ResourceLocator createLocator() {
        return new LocatorAdapter(new TestRemoteStoreService(store));
    }

    @Override
    public UserResource get(@InjectParam AuthenticatedUser user, ResourceId resourceId) {
        return store.get(user, resourceId);
    }

    @Override
    public List<Resource> getAccessControlRules(@InjectParam AuthenticatedUser user, ResourceId resourceId) {
        return store.getAccessControlRules(user, resourceId);
    }

    @Override
    @DELETE
    @Path("resource/{id}")
    @Consumes("application/json")
    @Produces("application/json")
    public UpdateResult delete(@InjectParam AuthenticatedUser user, ResourceId resourceId) {
        return store.delete(user, resourceId);
    }

    @Override
    public UpdateResult put(AuthenticatedUser user, Resource resource) {
        this.lastUpdated = resource.copy();
        return store.put(user, resource);
    }

    @Override
    public UpdateResult create(AuthenticatedUser user, Resource resource) {
        this.lastUpdated = resource.copy();
        return store.create(user, resource);
    }

    @Override
    public FolderProjection queryTree(@InjectParam AuthenticatedUser user, FolderRequest request) {
        return store.queryTree(user, request);
    }

    @Override
    @POST
    @Path("query/cube")
    @Consumes("application/json")
    @Produces("application/json")
    public List<Bucket> queryCube(@InjectParam AuthenticatedUser user, PivotTableModel tableModel) {
        return store.queryCube(user, tableModel);
    }

    @Override
    public List<ResourceNode> getOwnedOrSharedWorkspaces(@InjectParam AuthenticatedUser user) {
        return store.getOwnedOrSharedWorkspaces(user);
    }

    @Override
    public List<Resource> getUpdates(@InjectParam AuthenticatedUser user, ResourceId workspaceId, long version) {
        return store.getUpdates(user, workspaceId, version);
    }

    @Override
    public StoreLoader beginLoad(AuthenticatedUser user, ResourceId parentId) {
        return store.beginLoad(user, parentId);
    }

    @Override
    public StoreReader openReader(AuthenticatedUser user) {
        return store.openReader(user);
    }

    public Resource getLastUpdated() {
        return lastUpdated;
    }

}
