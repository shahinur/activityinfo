package org.activityinfo.model.resource;

import javax.annotation.Nonnull;

/**
 * Collision-Resistant Unique ID.
 *
 */
public final class ResourceId {

    @Nonnull
    private final String value;

    private ResourceId(String value) {
        assert value != null;
        this.value = value;
    }

    public static ResourceId create(String value) {
        return new ResourceId(value);
    }

    public String asString() {
        return value;
    }

    public char getDomain() {
        return value.charAt(0);
    }

    @Override
    public String toString() {
        return "<" + value + ">";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceId resourceId = (ResourceId) o;

        return value.equals(resourceId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
