package org.activityinfo.ui.app.server;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.collect.Sets;
import com.sun.jersey.api.core.DefaultResourceConfig;
import org.activityinfo.model.json.ObjectMapperFactory;
import org.activityinfo.store.test.TestResourceStore;

import java.io.IOException;
import java.util.Set;

public class TestApplication extends DefaultResourceConfig {


    @Override
    public Set<Object> getSingletons() {
        TestResourceStore store = loadStore();
        JacksonJsonProvider jsonProvider = new JacksonJsonProvider(ObjectMapperFactory.get());

        return Sets.newHashSet(store, jsonProvider);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return Sets.newHashSet(HostPage.class, TestResourceStore.class);
    }

    private TestResourceStore loadStore() {
        try {
            return new TestResourceStore().load("test.json");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load test database");
        }
    }
}
