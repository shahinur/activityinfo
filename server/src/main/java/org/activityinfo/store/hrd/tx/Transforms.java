package org.activityinfo.store.hrd.tx;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.common.base.Function;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Transforms {

    /**
     * Creates a {@code Function} that wraps a datastore Entity as an instance of {@code IsKey}
     *
     * @param K a class implementing {@link IsKey} with a
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
     *
     * @param T a class implementing {@link IsEntity} with a
     *          constructor accepting a single argument of {@link com.google.appengine.api.datastore.Entity}
     * @throws java.lang.IllegalArgumentException if the class does not have the required constructor
     */
    public static <T extends IsEntity> Function<Entity, T> entityWrapper(Class<T> clazz) {
        final Constructor<T> constructor = getConstructor(clazz, Entity.class);
        return new Function<Entity, T>() {

            @Override
            public T apply(Entity input) {
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

    private static <T, R> Constructor<R> getConstructor(Class<R> clazz, Class<T> argumentClass) {
        try {
            return clazz.getConstructor(argumentClass);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(String.format("Class %s has no constructor taking %s",
                clazz.getName(), argumentClass.getName()));
        }
    }
}
