package org.activityinfo.model.resource;

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
}
