package org.activityinfo.server.endpoint.odk;

class TestAuthenticationTokenService implements AuthenticationTokenService {
    final private static AuthenticationToken authenticationToken = new AuthenticationToken("AAAAAAAA");

    @Override
    public AuthenticationToken getAuthenticationToken(int userId, int formClassId) {
        return authenticationToken;
    }

    @Override
    public int getFormClassId(AuthenticationToken authenticationToken) {
        if (this.authenticationToken.equals(authenticationToken)) return 1081;
        else return 0;
    }

    @Override
    public int getUserId(AuthenticationToken authenticationToken) {
        return 0;
    }
}
