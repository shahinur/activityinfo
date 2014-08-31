package org.activityinfo.store.hrd.entity;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.Resource;

/**
 * An entity in the Resource entity group that stores the properties
 * and author of a given version of a resource.
 */
public class Snapshot {

    public static final String KIND = "S";

    public static final String TIMESTAMP_PROPERTY = "t";

    public static final String USER_PROPERTY = "u";

    private ResourceGroup group;
    private Key key;

    public Snapshot(ResourceGroup group, long version) {
        this.key = KeyFactory.createKey(group.getKey(), KIND, version);
        this.group = group;
    }


    public Entity createEntity(AuthenticatedUser user, Resource resource) {
        Entity entity = new Entity(key);
        entity.setProperty(TIMESTAMP_PROPERTY, System.currentTimeMillis());
        entity.setUnindexedProperty(USER_PROPERTY, user.getId());

        Content.writeProperties(resource, entity);
        return entity;
    }

}
