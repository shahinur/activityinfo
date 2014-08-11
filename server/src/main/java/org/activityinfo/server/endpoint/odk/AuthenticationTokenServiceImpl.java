package org.activityinfo.server.endpoint.odk;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;

public class AuthenticationTokenServiceImpl implements AuthenticationTokenService {
    @Override
    public AuthenticationToken getAuthenticationToken(int userId, int formClassId) {
        AuthenticationToken authenticationToken;
        Key key;
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Transaction transaction = datastoreService.beginTransaction();

        for (;;) {
            authenticationToken = new AuthenticationToken();
            key = KeyFactory.createKey("AuthenticationToken", authenticationToken.getToken());
            try {
                datastoreService.get(transaction, key);
                // The probability of generating a duplicate token is very small, so this block will almost always throw
            } catch (EntityNotFoundException entityNotFoundException) {
                // This is what we want, namely a unique authentication token
                break;
            }
        }

        Entity authEntity = new Entity(key);
        authEntity.setUnindexedProperty("userId", userId);
        authEntity.setUnindexedProperty("formClassId", formClassId);
        authEntity.setUnindexedProperty("creationTime", System.currentTimeMillis());
        datastoreService.put(transaction, authEntity);
        transaction.commit();

        return authenticationToken;
    }

    @Override
    public int getFormClassId(AuthenticationToken authenticationToken) throws Exception {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

        Entity authEntity =
                datastoreService.get(KeyFactory.createKey("AuthenticationToken", authenticationToken.getToken()));

        Long formClassId = (Long) authEntity.getProperty("formClassId");
        return formClassId.intValue();
    }

    @Override
    public int getUserId(AuthenticationToken authenticationToken) throws Exception {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

        Entity authEntity =
                datastoreService.get(KeyFactory.createKey("AuthenticationToken", authenticationToken.getToken()));

        Long userId = (Long) authEntity.getProperty("userId");
        return userId.intValue();
    }
}
