package org.activityinfo.ui.store.remote.server;

import com.google.common.collect.Sets;
import com.sun.jersey.api.core.ApplicationAdapter;
import com.sun.jersey.simple.container.SimpleServerFactory;
import org.activityinfo.store.test.TestResourceStore;

import javax.ws.rs.core.Application;
import java.io.Closeable;
import java.io.IOException;
import java.util.Set;

public class TestApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return Sets.<Class<?>>newHashSet(TestResourceStore.class);
    }

    public static Closeable start(int port) throws IOException {
        return SimpleServerFactory.create("http://localhost:" + port,
                new ApplicationAdapter(new TestApplication()));
    }
}
