package org.activityinfo.server.endpoint.odk;

class TestAuthenticationTokenService implements AuthenticationTokenService {
    private static final AuthenticationToken AUTH_TOKEN = new AuthenticationToken("AAAAAAAA");

    private static final AuthenticationToken FORM_MIME_TOKEN = new AuthenticationToken("LDbRuQsl");

    @Override
    public AuthenticationToken createAuthenticationToken(int userId, int formClassId) {
        return AUTH_TOKEN;
    }

    @Override
    public int getFormClassId(AuthenticationToken authenticationToken) {
        if (AUTH_TOKEN.equals(authenticationToken)) {
            return 1081;
        } else if(FORM_MIME_TOKEN.equals(authenticationToken)) {
            return 11218;
        } else {
            return 0;
        }
    }

    @Override
    public int getUserId(AuthenticationToken authenticationToken) {
        return 9944;
    }
}
