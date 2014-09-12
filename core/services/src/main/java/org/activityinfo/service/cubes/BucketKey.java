package org.activityinfo.service.cubes;

import java.util.Arrays;

public class BucketKey {
    private String[] dim;
    private int hashCode;

    public BucketKey(String[] dim) {
        this.dim = Arrays.copyOf(dim, dim.length);
        this.hashCode = Arrays.hashCode(dim);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BucketKey bucketKey = (BucketKey) o;

        return Arrays.equals(dim, bucketKey.dim);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    public BucketKey flyweight(String[] dim) {
        this.dim = dim;
        this.hashCode = Arrays.hashCode(dim);
        return this;
    }

    public String[] getDimensionValues() {
        return dim;
    }
}
