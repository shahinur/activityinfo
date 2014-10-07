package org.activityinfo.store.hrd.tx;

import com.google.appengine.api.datastore.*;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;

final class Operations {
    private Operations() {}


    public static <T extends IsEntity> T getOrThrow(DatastoreService datastore, Transaction transaction, IsKey<T> key) {
        Entity entity;
        try {
            entity = datastore.get(transaction, key.unwrap());
        } catch (EntityNotFoundException e) {
            throw new IllegalStateException("Entity " + key + " does not exist");
        }

        return key.wrapEntity(entity);
    }

    public static <T extends IsEntity> Optional<T> getIfExists(DatastoreService datastore, Transaction transaction, IsKey<T> key) {
        Entity entity;
        try {
            entity = datastore.get(transaction, key.unwrap());
        } catch (EntityNotFoundException e) {
            return Optional.absent();
        }

        return Optional.of(key.wrapEntity(entity));
    }

    public static <T extends IsEntity> Iterable<T> getList(DatastoreService datastore, Transaction transaction, List<? extends IsKey<T>> keys) {
        List<Key> unwrappedKeys = Lists.newArrayList();
        for(IsKey<T> key : keys) {
            unwrappedKeys.add(key.unwrap());
        }
        List<T> wrapped = Lists.newArrayList();
        Map<Key, Entity> keyEntityMap = datastore.get(transaction, unwrappedKeys);
        for(IsKey<T> key : keys) {
            Entity entity = keyEntityMap.get(key.unwrap());
            if(entity != null) {
                wrapped.add(key.wrapEntity(entity));
            }
        }
        return wrapped;
    }

    public static <T> Optional<T> query(DatastoreService datastore, Transaction transaction, SingleResultQuery<T> query) {
        if(query.isEmpty()) {
            return Optional.absent();
        } else {
            Iterator<Entity> results = datastore.prepare(transaction, query.getDatastoreQuery()).asIterator(withLimit(1));
            if(results.hasNext()) {
                return Optional.of(query.transform(results.next()));
            } else {
                return Optional.absent();
            }
        }
    }
}
