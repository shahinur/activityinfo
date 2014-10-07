package org.activityinfo.store.hrd.entity.user;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import org.activityinfo.model.auth.AuthenticatedUser;

public class UserEntityGroup {

    private static final String KIND = "User";
    private final Key rootKey;

    public UserEntityGroup(AuthenticatedUser user) {
        rootKey = KeyFactory.createKey(KIND, user.getUserResourceId().asString());
    }

    public Key getRootKey() {
        return rootKey;
    }
}
