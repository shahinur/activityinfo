package org.activityinfo.server.entity.auth;

import org.activityinfo.service.auth.AuthenticatedUser;


public interface AuthorizationHandler<T> {

    boolean isAuthorized(AuthenticatedUser user, T entity);

}
