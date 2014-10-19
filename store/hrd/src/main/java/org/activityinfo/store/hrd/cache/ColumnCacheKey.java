package org.activityinfo.store.hrd.cache;

import org.activityinfo.model.resource.ResourceId;

import java.io.Serializable;

/**
* Created by alex on 10/19/14.
*/
public class ColumnCacheKey implements Serializable {
    private String formClassId;
    private long formCacheKey;
    private String columnKey;

    protected ColumnCacheKey() {}

    public ColumnCacheKey(ResourceId formClassId, long formCacheKey, String columnKey) {
        this.formClassId = formClassId.asString();
        this.formCacheKey = formCacheKey;
        this.columnKey = columnKey;
    }

    public String getFormClassId() {
        return formClassId;
    }

    public long getFormCacheKey() {
        return formCacheKey;
    }

    public String getColumnKey() {
        return columnKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColumnCacheKey that = (ColumnCacheKey) o;

        if (formCacheKey != that.formCacheKey) return false;
        if (!columnKey.equals(that.columnKey)) return false;
        if (!formClassId.equals(that.formClassId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = formClassId.hashCode();
        result = 31 * result + (int) (formCacheKey ^ (formCacheKey >>> 32));
        result = 31 * result + columnKey.hashCode();
        return result;
    }
}
