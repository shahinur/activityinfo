package org.activityinfo.store.hrd;

import com.google.inject.servlet.ServletModule;
import org.activityinfo.service.store.ResourceStore;

public class HrdResourceStoreModule extends ServletModule {

    @Override
    protected void configureServlets() {
        bind(ResourceStore.class).to(HrdResourceStore.class);
    }
}
