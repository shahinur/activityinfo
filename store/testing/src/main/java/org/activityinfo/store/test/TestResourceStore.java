package org.activityinfo.store.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.model.table.Bucket;
import org.activityinfo.model.table.InstanceLabelTable;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.cubes.CubeBuilder;
import org.activityinfo.service.store.*;
import org.activityinfo.service.tables.TableBuilder;

import javax.annotation.Nullable;
import javax.ws.rs.PathParam;
import java.io.IOException;
import java.net.URL;
import java.util.*;

@SuppressWarnings("GwtClientClassFromNonInheritedModule")
public class TestResourceStore implements ResourceStore, StoreAccessor {

    private final Map<ResourceId, Resource> resourceMap = Maps.newHashMap();

    private AuthenticatedUser currentUser = new AuthenticatedUser();

    private int currentVersion = 1;

    private Resource lastUpdated;

    /**
     * Loads a set of resources from a json resource on the classpath
     * @param resourceName the name of the class path resource to load
     * @return this {@code TestResourceStore}, for chaining
     * @throws IOException
     */
    public TestResourceStore load(String resourceName) throws IOException {
        ObjectMapper mapper = ObjectMapperFactory.get();
        URL resourceURL = Resources.getResource(resourceName);
        Resource[] resources = mapper.readValue(resourceURL, Resource[].class);
        for(int i=0;i!=resources.length;++i) {
            resources[i].setVersion(currentVersion++);
            resourceMap.put(resources[i].getId(), resources[i]);
        }
        return this;
    }

    @Override
    public ResourceCursor openCursor(ResourceId formClassId) {
        List<Resource> resources = Lists.newArrayList();
        for(Resource resource : resourceMap.values()) {
            if(formClassId.equals(resource.getValue().getClassId())) {
                resources.add(resource);
            }
        }
        return new Cursor(resources.iterator());
    }

    public Resource get(ResourceId resourceId) {
        Resource resource = resourceMap.get(resourceId);
        if(resource == null) {
            throw new IllegalArgumentException("no such resource: " + resourceId);
        }
        return resource.copy();
    }

    @Override
    public UserResource get(@InjectParam AuthenticatedUser user, ResourceId resourceId) {
        return UserResource.userResource(get(resourceId));
    }

    private Set<Resource> get(AuthenticatedUser user, Set<ResourceId> resourceIds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FolderProjection queryTree(AuthenticatedUser user, FolderRequest request) {
        ResourceNode root = createNode(request, get(request.getRootId()));
        return new FolderProjection(root);
    }

    private UpdateResult put(AuthenticatedUser user, ResourceId id, Resource resource) {
        lastUpdated = resource.copy();
        lastUpdated.setVersion(currentVersion++);
        put(lastUpdated);
        return UpdateResult.committed(id, lastUpdated.getVersion());
    }

    @Override
    public UpdateResult delete(@InjectParam AuthenticatedUser user, ResourceId resourceId) {
        Resource resource = resourceMap.get(resourceId);
        if (resource != null) {
            long version = resource.getVersion() + 1;
            resourceMap.remove(resourceId);
            return UpdateResult.committed(resourceId, version);
        } else {
            return UpdateResult.rejected(resourceId);
        }
    }

    @Override
    public List<ResourceNode> getOwnedOrSharedWorkspaces(@InjectParam AuthenticatedUser user) {
        List<ResourceNode> nodes = Lists.newArrayList();
        for(Resource resource : all()) {
            if(resource.getOwnerId().equals(org.activityinfo.model.resource.Resources.ROOT_ID)) {
                nodes.add(newNode(resource));
            }
        }
        return nodes;
    }

    private ResourceNode newNode(Resource resource) {
        ResourceId classId = resource.getValue().getClassId();
        ResourceNode node = new ResourceNode(resource.getId(), classId);
        node.setOwnerId(resource.getOwnerId());

        if(classId.equals(FormClass.CLASS_ID)) {
            node.setLabel(resource.getValue().isString(FormClass.LABEL_FIELD_ID));
        } else if(classId.equals(FolderClass.CLASS_ID)) {
            node.setLabel(resource.getValue().isString(FolderClass.LABEL_FIELD_NAME));
        }
        return node;
    }

    @Override
    public TableData queryTable(@InjectParam AuthenticatedUser user, TableModel tableModel) {
        TableBuilder builder = new TableBuilder(this);
        try {
            return builder.buildTable(tableModel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Bucket> queryCube(@InjectParam AuthenticatedUser user, PivotTableModel tableModel) {
        try {
            return new CubeBuilder(this).buildCube(tableModel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UpdateResult put(AuthenticatedUser user, Resource resource) {
        return put(currentUser, resource.getId(), resource);
    }

    @Override
    public UpdateResult create(AuthenticatedUser user, Resource resource) {
        if (resourceMap.get(resource.getId()) == null) return put(user, resource);
        else return UpdateResult.rejected();
    }

    @Override
    public void close() {

    }

    private ResourceNode createNode(FolderRequest request, Resource resource) {
        ResourceId classId = getClassId(resource);
        ResourceNode node = new ResourceNode(resource.getId(), classId);
        node.setLabel(getLabel(resource, classId));

        // add children
        for(Resource child : resourceMap.values()) {
            if(child.getOwnerId().equals(resource.getId()) && request.getFormClassIds().contains(classId)) {
                node.getChildren().add(createNode(request, child));
            }
        }
        return node;
    }

    @Override
    public List<Resource> getAccessControlRules(@InjectParam AuthenticatedUser user,
                                                @PathParam("id") ResourceId resourceId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Resource> getUpdates(@InjectParam AuthenticatedUser user, ResourceId workspaceId, long version) {
        throw new UnsupportedOperationException();
    }

    @Override
    public StoreLoader beginLoad(AuthenticatedUser user, ResourceId parentId) {
        throw new UnsupportedOperationException();
    }

    private String getLabel(Resource resource, ResourceId classId) {
        if(FormClass.CLASS_ID.equals(classId)) {
            return resource.getValue().getString(FormClass.LABEL_FIELD_ID);
        } else if(FolderClass.CLASS_ID.equals(classId)) {
            return resource.getValue().getString(FolderClass.LABEL_FIELD_ID.asString());
        } else {
            return classId == null ? resource.getId().asString() : classId.asString();
        }
    }

    private ResourceId getClassId(Resource resource) {
        return resource.getValue().getClassId();
    }

    public void put(Resource resource) {
        resourceMap.put(resource.getId(), resource.copy());
    }

    public Iterable<Resource> all() {
        return resourceMap.values();
    }

    public void remove(ResourceId id) {
        resourceMap.remove(id);
    }

    public Resource getLastUpdated() {
        return lastUpdated.copy();
    }

    public ResourceLocator createLocator() {
        return new LocatorAdapter(new TestRemoteStoreService(this));
    }

    public static ResourceLocator createLocator(String jsonResourceName) throws IOException {
        return new TestResourceStore().load(jsonResourceName).createLocator();
    }

    private class Cursor implements ResourceCursor {

        private Iterator<Resource> iterator;

        private Cursor(Iterator<Resource> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Resource next() {
            return iterator.next().copy();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void close() throws Exception {

        }
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
