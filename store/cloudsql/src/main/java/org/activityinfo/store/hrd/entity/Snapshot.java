package org.activityinfo.store.hrd.entity;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;

/**
 * An entity in the Resource entity group that stores the properties
 * and author of a given version of a resource.
 */
public class Snapshot {

    public static final String KIND = "S";

    public static final String PARENT_KIND = "R";

    public static final String TIMESTAMP_PROPERTY = "t";

    public static final String USER_PROPERTY = "u";

    private Key key;

    public Snapshot(Key rootKey, ResourceId id, long version) {
        Key parentKind = KeyFactory.createKey(rootKey, PARENT_KIND, id.asString());
        this.key = KeyFactory.createKey(parentKind, KIND, version);
    }


    public Entity createEntity(AuthenticatedUser user, Resource resource) {
        Entity entity = new Entity(key);
        entity.setProperty(TIMESTAMP_PROPERTY, System.currentTimeMillis());
        entity.setUnindexedProperty(USER_PROPERTY, user.getId());

        Content.writeProperties(resource, entity);
        return entity;
    }

}
