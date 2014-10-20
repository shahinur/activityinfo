package org.activityinfo.service.identity.entity;

import com.google.appengine.api.datastore.Entity;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.activityinfo.service.identity.UserId;
import org.activityinfo.store.hrd.tx.IsEntity;

public class UserAccount implements IsEntity {

    public static final String EMAIL_PROPERTY = "email";
    public static final String NAME_PROPERTY = "name";

    private final UserAccountKey key;
    private String name;
    private String email;

    public UserAccount(UserAccountKey key, Entity entity) {
        this.key = key;
        this.email = (String)entity.getProperty(EMAIL_PROPERTY);
        this.name = (String)entity.getProperty(NAME_PROPERTY);
    }

    public UserAccount(UserId userId) {
        this.key = new UserAccountKey(userId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Entity toEntity() {
        Preconditions.checkState(!Strings.isNullOrEmpty(email), "email is not set");
        Preconditions.checkState(!Strings.isNullOrEmpty(name), "user name is not set");

        Entity entity = new Entity(key.unwrap());
        entity.setProperty(EMAIL_PROPERTY, email);
        entity.setUnindexedProperty(NAME_PROPERTY, name);
        return entity;
    }

    public UserId getUserId() {
        return key.getUserId();
    }
}
