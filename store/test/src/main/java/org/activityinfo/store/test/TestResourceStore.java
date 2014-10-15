package org.activityinfo.store.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.model.analysis.PivotTableModel;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.json.ObjectMapperFactory;
import org.activityinfo.model.legacy.InstanceQuery;
import org.activityinfo.model.legacy.Projection;
import org.activityinfo.model.legacy.QueryResult;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.system.ApplicationClassProvider;
import org.activityinfo.model.table.Bucket;
import org.activityinfo.model.table.InstanceLabelTable;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.store.*;
import org.activityinfo.store.hrd.HrdResourceStore;
import org.activityinfo.store.hrd.StoreContext;
import org.activityinfo.store.hrd.dao.WorkspaceCreation;

import javax.annotation.Nullable;
import javax.ws.rs.*;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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

    /**
     * Exposes a legacy {@code Dispatcher} implementation as new {@code ResourceLocator}
     */
    private static class LocatorAdapter implements ResourceLocator {

        private final ApplicationClassProvider systemClassProvider = new ApplicationClassProvider();


        private final RemoteStoreService store;

        private final ProjectionAdapter projectionAdapter;

        public LocatorAdapter(RemoteStoreService tableService) {
            this.store = tableService;
            this.projectionAdapter = new ProjectionAdapter(tableService);
        }

        @Override
        public Promise<FormClass> getFormClass(ResourceId classId) {
            if(classId.asString().startsWith("_")) {
                return Promise.resolved(systemClassProvider.get(classId));
            } else {
                return store.get(classId).then(new Function<UserResource, FormClass>() {
                    @Nullable
                    @Override
                    public FormClass apply(@Nullable UserResource input) {
                        return FormClass.fromResource(input.getResource());
                    }
                });
            }
        }

        @Override
        public Promise<FormInstance> getFormInstance(ResourceId instanceId) {
            return store.get(instanceId).then(new Function<UserResource, FormInstance>() {
                @Nullable
                @Override
                public FormInstance apply(@Nullable UserResource input) {
                    return FormInstance.fromResource(input.getResource());
                }
            });
        }

        @Override
        public Promise<List<UserResource>> get(Set<ResourceId> resourceIds) {
            return Promise.map(resourceIds, new Function<ResourceId, Promise<UserResource>>() {
                @Override
                public Promise<UserResource> apply(ResourceId input) {
                    return store.get(input);
                }
            });
        }

        @Override
        public Promise<TableData> queryTable(TableModel tableModel) {
            return store.queryTable(tableModel);
        }

        @Override
        public Promise<Void> persist(IsResource resource) {
            return store.put(resource.asResource()).thenDiscardResult();
        }

        @Override
        public Promise<Void> persist(List<? extends IsResource> resources) {
            final List<Promise<Void>> promises = Lists.newArrayList();
            if (resources != null && !resources.isEmpty()) {
                for (final IsResource resource : resources) {
                    promises.add(persist(resource));
                }
            }
            return Promise.waitAll(promises);
        }

        @Override
        public Promise<QueryResult> queryProjection(InstanceQuery query) {
            return projectionAdapter.query(query);
        }

        @Override
        public Promise<List<Projection>> query(InstanceQuery query) {
            return projectionAdapter.query(query).then(new Function<QueryResult, List<Projection>>() {
                @Override
                public List<Projection> apply(QueryResult input) {
                    return input.getProjections();
                }
            });
        }

        @Override
        public Promise<Void> remove(Collection<ResourceId> resources) {
            List<Promise<?>> promises = Lists.newArrayList();
            for (ResourceId resource : resources) {
                promises.add(store.remove(resource));
            }
            return Promise.waitAll(promises);
        }

        @Override
        public Promise<InstanceLabelTable> queryFormList() {
            throw new UnsupportedOperationException();
        }
    }
}
