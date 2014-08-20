package org.activityinfo.server.util.jaxrs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
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
        bind(ObjectMapper.class).toInstance(ObjectMapperFactory.get());
        bind(JacksonJsonProvider.class).to(Utf8JacksonJsonProvider.class).in(Singleton.class);
    }
}
