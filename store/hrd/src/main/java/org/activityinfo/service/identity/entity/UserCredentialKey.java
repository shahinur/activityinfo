package org.activityinfo.service.identity.entity;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import org.activityinfo.service.identity.UserId;
import org.activityinfo.store.hrd.tx.IsKey;

public class UserCredentialKey implements IsKey<UserCredential> {

    private static final String KIND = "UserCredential";

    private final Key key;

    public UserCredentialKey(UserId userId) {
        key = KeyFactory.createKey(new UserAccountKey(userId).unwrap(), KIND, userId.longValue());
    }

    @Override
    public Key unwrap() {
        return key;
    }

    @Override
    public UserCredential wrapEntity(Entity entity) {
        return new UserCredential(this, entity);
    }
}
