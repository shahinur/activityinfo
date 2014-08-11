package org.activityinfo.server.endpoint.odk;

import com.google.inject.ImplementedBy;

@ImplementedBy(AuthenticationTokenServiceImpl.class)
public interface AuthenticationTokenService {
    public AuthenticationToken getAuthenticationToken(int userId, int formClassId);

    public int getFormClassId(AuthenticationToken authenticationToken) throws Exception;

    public int getUserId(AuthenticationToken authenticationToken) throws Exception;
}
