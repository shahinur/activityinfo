package org.activityinfo.server.endpoint.odk;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.inject.Inject;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.server.authentication.ServerSideAuthProvider;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.security.SecureRandom;

public class AuthenticationTokenServiceImpl implements AuthenticationTokenService {
    private static final String PREFIX = "token:";
    public static final String KIND = "XFormAuthToken";
    private DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    private ServerSideAuthProvider authProvider;

    @Inject
    public AuthenticationTokenServiceImpl(ServerSideAuthProvider authProvider) {
        this.authProvider = authProvider;
    }

    @Override
    public String createAuthenticationToken(int userId, ResourceId formClassId) {
        String token = PREFIX + Long.toHexString(new SecureRandom().nextLong());
        Entity entity = new Entity(key(token));
        entity.setUnindexedProperty("userId", userId);
        entity.setUnindexedProperty("formClassId", formClassId.asString());
        entity.setUnindexedProperty("creationTime", System.currentTimeMillis());
        datastoreService.put(null, entity);

        return token;
    }

    private Key key(String token) {
        return KeyFactory.createKey(KIND, token);
    }

    @Override
    public AuthenticatedUser authenticate(String authenticationToken) {
        Entity authEntity = null;
        try {
            authEntity = datastoreService.get(key(authenticationToken));
        } catch (EntityNotFoundException e) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        long userId = (Long) authEntity.getProperty("userId");
        AuthenticatedUser user = new AuthenticatedUser("", (int) userId, "@");
        authProvider.set(user);

        return user;
    }

    @Override
    public long getLong(String authenticationToken) {
        return staticLong(authenticationToken);
    }

    public static long staticLong(String authenticationToken) {
        if (authenticationToken != null) {
            if (authenticationToken.substring(0, PREFIX.length()).equals(PREFIX)) {
                return stringToLong(authenticationToken.substring(PREFIX.length()));
            }
        }

        throw new IllegalArgumentException("Input token is not a valid authentication token");
    }

    /** This bizarre method is necessary because Java's parser can't deal with hex values greater than 2 ** 63 - 1... */
    private static long stringToLong(String string) {
        final int length = string.length();

        if (length < 16) {
            return Long.parseLong(string, 16);
        } else if (length > 16) {
            throw new IllegalArgumentException("Input token is not a valid authentication token");
        }

        switch (Character.toUpperCase(string.charAt(0))) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
                return Long.parseLong(string, 16);

            case '8':
                return Long.parseLong('0' + string.substring(1), 16) + Long.MIN_VALUE;

            case '9':
                return Long.parseLong('1' + string.substring(1), 16) + Long.MIN_VALUE;

            case 'A':
                return Long.parseLong('2' + string.substring(1), 16) + Long.MIN_VALUE;

            case 'B':
                return Long.parseLong('3' + string.substring(1), 16) + Long.MIN_VALUE;

            case 'C':
                return Long.parseLong('4' + string.substring(1), 16) + Long.MIN_VALUE;

            case 'D':
                return Long.parseLong('5' + string.substring(1), 16) + Long.MIN_VALUE;

            case 'E':
                return Long.parseLong('6' + string.substring(1), 16) + Long.MIN_VALUE;

            case 'F':
                return Long.parseLong('7' + string.substring(1), 16) + Long.MIN_VALUE;

            default:
                throw new IllegalArgumentException("Input token is not a valid authentication token");
        }
    }
}
