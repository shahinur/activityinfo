package org.activityinfo.service.store;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.collect.Sets;
import com.sun.jersey.api.core.DefaultResourceConfig;
import org.activityinfo.model.json.ObjectMapperFactory;
import org.activityinfo.service.jaxrs.UpdateResultWriter;

import java.util.Set;

public class TestResourceConfig extends DefaultResourceConfig {

    @Override
    public Set<Class<?>> getClasses() {
        return Sets.<Class<?>>newHashSet(UpdateResultWriter.class, ResourceStoreStub.class);
    }

    @Override
    public Set<Object> getSingletons() {
        return Sets.<Object>newHashSet(new JacksonJsonProvider(ObjectMapperFactory.get()));
    }
}
