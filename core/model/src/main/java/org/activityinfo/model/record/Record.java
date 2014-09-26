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

}