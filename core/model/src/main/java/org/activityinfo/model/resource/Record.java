package org.activityinfo.model.resource;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;

/**
 * A record is a collection of named properties that
 * may be embedded within a {@code Resource}
 *
 * Unlike a resource, individual {@code Record}s are not
 * required to have a stable, globally unique identity.
 */
public final class Record extends PropertyBag<Record> {

    @Override
    public String toString() {
        return getProperties().toString();
    }

    @JsonValue
    @Override
    public Map<String, Object> getProperties() {
        // Overriden in order to apply the @JsonValue annotation
        // specifically for Records
        return super.getProperties();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Record) {
            Record otherRecord = (Record) o;
            return this.getProperties().equals(otherRecord.getProperties());
        }
        return false;
    }
}
