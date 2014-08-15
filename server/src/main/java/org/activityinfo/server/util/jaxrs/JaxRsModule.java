package org.activityinfo.server.util.jaxrs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.servlet.ServletModule;
import org.activityinfo.model.json.ObjectMapperFactory;

import javax.inject.Singleton;

/**
 * Provides the basic configuration for the Jersey Container,
 * including the FreeMarker ViewProcessor and the
 * Jackson Json Provider.
 */
public class JaxRsModule extends ServletModule {

    @Override
    protected void configureServlets() {
        bind(FreemarkerViewProcessor.class);
        bind(Utf8JacksonJsonProvider.class).in(Singleton.class);
        bind(ObjectMapper.class).toInstance(ObjectMapperFactory.get());
    }
}
