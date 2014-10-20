package org.activityinfo.service.identity.entity;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import org.activityinfo.service.identity.UserId;
import org.activityinfo.store.hrd.tx.IsKey;


public class UserAccountKey implements IsKey<UserAccount> {

    private static final String KIND = "UserAccount";
    private final Key key;

    public UserAccountKey(UserId userId) {
        key = KeyFactory.createKey(KIND, userId.longValue());
    }

    @Override
    public Key unwrap() {
        return key;
    }

    @Override
    public UserAccount wrapEntity(Entity entity) {
        return new UserAccount(this, entity);
    }

    public UserId getUserId() {
        return new UserId(key.getId());
    }
}
