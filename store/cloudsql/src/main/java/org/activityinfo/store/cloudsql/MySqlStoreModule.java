package org.activityinfo.store.cloudsql;

import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.inject.Provides;
import com.google.inject.servlet.ServletModule;
import org.activityinfo.service.store.ResourceStore;

public class MySqlStoreModule extends ServletModule {
    @Override
    protected void configureServlets() {
        bind(ConnectionProvider.class);
        filter("/*").through(ConnectionCleanupFilter.class);
    }

    @Provides
    public ResourceStore provideResourceStore(ConnectionProvider connectionProvider) {
        return new MySqlResourceStore(connectionProvider, MemcacheServiceFactory.getMemcacheService());
    }

}
