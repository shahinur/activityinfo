package org.activityinfo.core.shared;

import javax.annotation.Nonnull;

/**
 * Collision-Resistant Unique ID.
 *
 */
public final class Cuid {

    @Nonnull
    private final String value;

    public Cuid(String value) {
        assert value != null;
        this.value = value;
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

        Cuid cuid = (Cuid) o;

        return value.equals(cuid.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
