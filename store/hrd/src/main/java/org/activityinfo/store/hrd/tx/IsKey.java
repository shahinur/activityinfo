package org.activityinfo.store.hrd.tx;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

public interface IsKey<T extends IsEntity> {

    Key unwrap();

    T wrapEntity(Entity entity);

}
