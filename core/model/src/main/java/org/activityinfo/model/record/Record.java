package org.activityinfo.model.record;

import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldValue;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * A record is a collection of field values that
 * may be embedded within a {@code Resource}
 *
 * Unlike a resource, individual {@code Record}s are not
 * required to have a stable, globally unique identity.
 */
public interface Record extends FieldValue {

    public ResourceId getClassId();

    public Object get(String fieldName);

    /**
     * @return {@code true} if this {@code Record} contains a value for the given field name.
     */
    public boolean has(String fieldName);

    public boolean getBoolean(String fieldName);

    public Boolean isBoolean(String fieldName);

    public boolean getBoolean(String fieldName, boolean defaultValue);

    @Nonnull
    public List<Record> getRecordList(String fieldName);

    @Nonnull
    public List<String> getStringList(String fieldName);

    @Nonnull
    public String getString(String fieldName);

    public String isString(String fieldName);

    public String getString(String fieldName, String defaultValue);

    @Nonnull
    public Record getRecord(String fieldName);

    public Record isRecord(String fieldName);

    public double getDouble(String fieldName);

    public int getInt(String fieldName);

    /**
     *
     * @return an immutable {@code Map} view of this {@code Record}
     */
    Map<String, Object> asMap();

    /**
     * Indicates whether another {@code Record} is equal to this one. Implementations of this method may call themselves
     * recursively for records contained within other records, because records aren't allowed to contain themselves. Put
     * differently, circular references are not allowed to exist, so they will not be able to cause any problems either.
     * @param record the reference {@code Record) with which to compare this {@code Record}.
     * @return {@code true} if this {@code Record} is the equal to the {@code record} argument, {@code false} otherwise.
     */
    public boolean deepEquals(Record record);
}
