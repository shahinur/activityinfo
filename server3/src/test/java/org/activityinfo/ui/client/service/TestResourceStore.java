package org.activityinfo.ui.client.service;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.service.auth.AuthenticatedUser;
import org.activityinfo.service.store.*;
import org.activityinfo.service.tables.TableBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        JsonParser parser = new JsonParser();
        JsonArray resourceArray;
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream(resourceName), Charsets.UTF_8)) {
            resourceArray = parser.parse(reader).getAsJsonArray();
        }

        for(int i=0;i!=resourceArray.size();++i) {
            Resource resource = Resources.fromJson(resourceArray.get(i).getAsJsonObject());
            resource.setVersion(currentVersion++);
            resourceMap.put(resource.getId(), resource);
        }
        return this;
    }

    @Override
    public ResourceCursor openCursor(ResourceId formClassId) {
        List<Resource> resources = Lists.newArrayList();
        for(Resource resource : resourceMap.values()) {
            if(formClassId.asString().equals(resource.isString("classId"))) {
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
    public Resource get(@InjectParam AuthenticatedUser user, ResourceId resourceId) {
        return get(resourceId);
    }

    @Override
    public Set<Resource> get(AuthenticatedUser user, Set<ResourceId> resourceIds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResourceTree queryTree(AuthenticatedUser user, ResourceTreeRequest request) {
        ResourceNode root = createNode(request, get(request.getRootId()));
        return new ResourceTree(root);
    }

    @Override
    public UpdateResult put(AuthenticatedUser user, ResourceId id, Resource resource) {
        int version = currentVersion++;
        resource.setVersion(version);
        lastUpdated = resource.copy();
        put(lastUpdated);
        return UpdateResult.committed(id, version);
    }

    @Override
    public List<ResourceNode> getUserRootResources(@InjectParam AuthenticatedUser user) {
        throw new UnsupportedOperationException();
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
    public UpdateResult put(AuthenticatedUser user, Resource resource) {
        return put(currentUser, resource.getId(), resource);
    }

    @Override
    public void close() {

    }

    private ResourceNode createNode(ResourceTreeRequest request, Resource resource) {
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

    private String getLabel(Resource resource, ResourceId classId) {
        if(FormClass.CLASS_ID.equals(classId)) {
            return resource.getString(FormClass.LABEL_FIELD_ID);
        } else if(FolderClass.CLASS_ID.equals(classId)) {
            return resource.getString(FolderClass.LABEL_FIELD_ID.asString());
        } else {
            return classId == null ? resource.getId().asString() : classId.asString();
        }
    }

    private ResourceId getClassId(Resource resource) {
        if(resource.has("classId")) {
            return ResourceId.valueOf(resource.getString("classId"));
        } else {
            return null;
        }
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

}
