package org.activityinfo.ui.client.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.service.store.ResourceCursor;
import org.activityinfo.service.store.ResourceStore;

import java.io.IOException;
import java.io.InputStreamReader;
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
        try (InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream(resourceName))) {
            resourceArray = parser.parse(reader).getAsJsonArray();
        }

        for(int i=0;i!=resourceArray.size();++i) {
            Resource resource = Resources.fromJson(resourceArray.get(i).getAsJsonObject());
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

    @Override
    public Resource get(ResourceId resourceId) {
        Resource resource = resourceMap.get(resourceId);
        if(resource == null) {
            throw new IllegalArgumentException("no such resource: " + resourceId);
        }
        return resource.copy();
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

    private class Cursor implements ResourceCursor {

        private Iterator<Resource> iterator;
        private Resource current;

        private Cursor(Iterator<Resource> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean next() {
            if(iterator.hasNext()) {
                current = iterator.next().copy();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public Resource getResource() {
            return current;
        }
    }

}
