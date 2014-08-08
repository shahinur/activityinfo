package org.activityinfo.ui.client.service;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.system.FolderClass;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TestResourceStore implements ResourceStore {

    private final Map<ResourceId, Resource> resourceMap = Maps.newHashMap();


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
            resourceMap.put(resource.getId(), resource);
        }
        return this;
    }

    @Override
    public Iterator<Resource> openCursor(ResourceId formClassId) {
        List<Resource> resources = Lists.newArrayList();
        for(Resource resource : resourceMap.values()) {
            if(formClassId.asString().equals(resource.isString("classId"))) {
                resources.add(resource);
            }
        }
        return new Cursor(resources.iterator());
    }

    @Override
    public Resource get(ResourceId resourceId) {
        Resource resource = resourceMap.get(resourceId);
        if(resource == null) {
            throw new IllegalArgumentException("no such resource: " + resourceId);
        }
        return resource.copy();
    }

    @Override
    public ResourceTree queryTree(ResourceTreeRequest request) {
        ResourceNode root = createNode(request, get(request.getRootId()));
        return new ResourceTree(root);
    }

    @Override
    public void createResource(ResourceId userId, Resource resource) {
        put(resource);
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
            return ResourceId.create(resource.getString("classId"));
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

    private class Cursor extends UnmodifiableIterator<Resource> {

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
    }

}
