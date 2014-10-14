package org.activityinfo.store.hrd.tx;

import com.google.appengine.api.datastore.*;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;

class Mapping {

    /**
     * Creates a {@code Function} that wraps a datastore Entity as an instance of {@code IsKey}
     *
     * @param clazz a class implementing {@link IsKey} with a
     *          constructor accepting a single argument of {@link com.google.appengine.api.datastore.Key}
     * @throws java.lang.IllegalArgumentException if the class does not have the required constructor
     */
    public static <K extends IsKey> Function<Entity, K> keyWrapper(Class<K> clazz) {
        final Constructor<K> constructor = getConstructor(clazz, Key.class);

        return new Function<Entity, K>() {

            @Override
            public K apply(Entity input) {
                try {
                    return constructor.newInstance(input.getKey());
                } catch (InstantiationException | IllegalAccessException e ) {
                    throw new UnsupportedOperationException("Failed to invoke constructor " + constructor, e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e.getCause());
                }
            }
        };
    }

    /**
     * Creates a {@code Function} that wraps a datastore Entity as an instance of {@code IsEntity}
     * @param clazz a class implementing {@link IsEntity} with a
     *          constructor accepting a single argument of {@link com.google.appengine.api.datastore.Entity}
     * @throws java.lang.IllegalArgumentException if the class does not have the required constructor
     */
    public static <T extends IsEntity> Function<Entity, T> entityWrapper(Class<T> clazz) {
        final Constructor<T> constructor = getConstructor(clazz, Entity.class);
        return new Function<Entity, T>() {

            @Override
            public T apply(Entity input) {
                try {
                    return constructor.newInstance(input);
                } catch (InstantiationException | IllegalAccessException e ) {
                    throw new UnsupportedOperationException("Failed to invoke constructor " + constructor, e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e.getCause());
                }
            }
        };
    }

    private static <T, R> Constructor<R> getConstructor(Class<R> clazz, Class<T> argumentClass) {
        try {
            return clazz.getConstructor(argumentClass);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(String.format("Class %s has no constructor taking %s",
                clazz.getName(), argumentClass.getName()));
        }
    }

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
