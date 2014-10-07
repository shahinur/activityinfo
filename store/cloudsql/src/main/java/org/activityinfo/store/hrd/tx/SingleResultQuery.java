package org.activityinfo.store.hrd.tx;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.common.base.Function;
import org.activityinfo.store.hrd.entity.workspace.Snapshot;

public class SingleResultQuery<T> {

    private final Query query;
    private final Function<Entity, T> transformer;

    private SingleResultQuery() {
        query = null;
        transformer = null;
    }

    public SingleResultQuery(Query query, Function<Entity, T> transformer) {
        this.query = query;
        this.transformer = transformer;
    }

    public boolean isEmpty() {
        return query != null;
    }

    public Query getDatastoreQuery() {
        return query;
    }

    public Function<Entity, T> getTransformer() {
        return transformer;
    }

    public static <T extends IsEntity> SingleResultQuery<T> create(Query query, Class<T> clazz) {
        return new SingleResultQuery<>(query, Mapping.entityWrapper(clazz));
    }

    public static <T extends IsKey> SingleResultQuery<T> createKeysOnly(Query query, Class<T> clazz) {
        return new SingleResultQuery<>(query, Mapping.keyWrapper(clazz));
    }

    public static <T> SingleResultQuery<Snapshot> empty(Class<T> clazz) {
        return new SingleResultQuery<>(null, null);
    }

    public T transform(Entity entity) {
        return transformer.apply(entity);
    }
}
