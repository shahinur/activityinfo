package org.activityinfo.ui.app.server;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.collect.Sets;
import com.sun.jersey.api.core.DefaultResourceConfig;
import org.activityinfo.model.json.ObjectMapperFactory;
import org.activityinfo.store.hrd.HrdResourceStore;

import java.util.Set;

public class TestApplication extends DefaultResourceConfig {


    public TestApplication() {
    }

    @Override
    public Set<Object> getSingletons() {
        JacksonJsonProvider jsonProvider = new JacksonJsonProvider(ObjectMapperFactory.get());

        return Sets.newHashSet((Object)jsonProvider);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return Sets.newHashSet(HostPage.class, HrdResourceStore.class);
    }


}
