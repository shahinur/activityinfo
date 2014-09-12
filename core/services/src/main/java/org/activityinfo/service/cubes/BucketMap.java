package org.activityinfo.service.cubes;

import com.google.common.base.Supplier;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BucketMap<T> {

    //private final BucketKey flyweightKey = new BucketKey(new String[0]);

    private final Map<BucketKey, T> map = new HashMap<>();
    private final Supplier<T> initialValue;

    public BucketMap(Supplier<T> initialValue) {
        this.initialValue = initialValue;
    }

    public T get(String[] dimValues) {
        BucketKey key = new BucketKey(dimValues);
        T bucket = map.get(key);
        if(bucket == null) {
            bucket = initialValue.get();
            map.put(key, bucket);
        }
        return bucket;
    }


    public java.util.Collection<T> values() {
        return map.values();
    }

    public Set<Map.Entry<BucketKey, T>> entrySet() {
        return map.entrySet();
    }
}
