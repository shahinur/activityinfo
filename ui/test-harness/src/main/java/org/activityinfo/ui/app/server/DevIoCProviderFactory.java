package org.activityinfo.ui.app.server;

import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.core.spi.component.ioc.IoCInstantiatedComponentProvider;
import org.activityinfo.model.auth.AuthenticatedUser;

/**
 * Fake IoC provider to replace Guice within the test harness
 */
public class DevIoCProviderFactory implements IoCComponentProviderFactory {

    @Override
    public IoCComponentProvider getComponentProvider(Class<?> c) {
        if(c.equals(AuthenticatedUser.class)) {
            return new IoCInstantiatedComponentProvider() {
                @Override
                public Object getInstance() {
                    return new AuthenticatedUser("XYZ", 1, "user@test.org");
                }

                @Override
                public Object getInjectableInstance(Object o) {
                    return o;
                }
            };
        } else {
            return null;
        }
    }

    @Override
    public IoCComponentProvider getComponentProvider(ComponentContext cc, Class<?> c) {
        return getComponentProvider(c);
    }
}
