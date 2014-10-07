package org.activityinfo.store.hrd.tx;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.common.base.Function;

public class ListQuery<T> {

    private final Query query;
    private final Function<Entity, T> transformer;

    public static <T extends IsEntity> ListQuery<T> create(Query query, Class<T> clazz) {
        return new ListQuery<>(query, Transforms.entityWrapper(clazz));
    }

    public static <T extends IsKey> ListQuery<T> createKeysOnly(Query query, Class<T> clazz) {
        return new ListQuery<>(query, Transforms.keyWrapper(clazz));
    }

    public ListQuery(Query query, Function<Entity, T> transformer) {
        this.query = query;
        this.transformer = transformer;
    }

    public Query getDatastoreQuery() {
        return query;
    }

    public Function<Entity, T> getTransformer() {
        return transformer;
    }
}
