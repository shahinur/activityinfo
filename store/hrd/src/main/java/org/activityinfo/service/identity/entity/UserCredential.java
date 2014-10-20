package org.activityinfo.service.identity.entity;

import com.google.appengine.api.datastore.Entity;
import org.activityinfo.service.identity.UserId;
import org.activityinfo.store.hrd.tx.IsEntity;

public class UserCredential implements IsEntity {
    private final UserCredentialKey key;
    private String hashedPassword;

    private static final String PASSWORD_PROPERTY = "pw";

    public UserCredential(UserCredentialKey key, Entity entity) {
        this.key = key;
        this.hashedPassword = (String) entity.getProperty(PASSWORD_PROPERTY);
    }

    public UserCredential(UserId userId) {
        this.key = new UserCredentialKey(userId);
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    @Override
    public Entity toEntity() {
        Entity entity = new Entity(key.unwrap());
        entity.setUnindexedProperty(PASSWORD_PROPERTY, hashedPassword);
        return entity;
    }
}
