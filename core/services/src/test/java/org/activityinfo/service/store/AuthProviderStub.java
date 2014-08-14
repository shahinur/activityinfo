package org.activityinfo.service.store;

import com.sun.jersey.api.core.InjectParam;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.PerRequestTypeInjectableProvider;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.ResourceId;

import javax.ws.rs.ext.Provider;

@Provider
public class AuthProviderStub extends PerRequestTypeInjectableProvider<InjectParam, AuthenticatedUser> {

    public static final int AUTHORIZED_USER_ID = 1;

    public static final ResourceId AUTHORIZED_USER_RESOURCE_ID = CuidAdapter.userId(AUTHORIZED_USER_ID);

    private AuthenticatedUser user;

    public AuthProviderStub() {
        super(AuthenticatedUser.class);
        user = new AuthenticatedUser("XYZ123", AUTHORIZED_USER_ID, "user@user.org");
    }

    @Override
    public Injectable<AuthenticatedUser> getInjectable(ComponentContext ic, InjectParam injectParam) {
        return new Injectable<AuthenticatedUser>() {
            @Override
            public AuthenticatedUser getValue() {
                return user;
            }
        };
    }
}
