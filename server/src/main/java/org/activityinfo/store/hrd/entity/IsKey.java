package org.activityinfo.store.hrd.entity;

import com.google.appengine.api.datastore.Key;

public interface IsKey<T> {

    Key create();


}
