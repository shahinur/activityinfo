package org.activityinfo.model.resource;


import javax.annotation.Nonnull;
import java.util.Date;

/**
 * Globally, universally unique and persistent identifier
 * for {@code Resources}
 *
 */
public final class ResourceId {

    public static final ResourceId ROOT_ID = ResourceId.create("_root");

    public static final int RADIX = 10;
    public static long COUNTER = 1;

    private final String text;

    public static ResourceId create(@Nonnull String string) {
        assert string != null;
        return new ResourceId(string);
    }

    public static ResourceId generateId() {
        return create("c" + Long.toString(new Date().getTime(), Character.MAX_RADIX) +
               Long.toString(COUNTER++, Character.MAX_RADIX));
    }


    private ResourceId(@Nonnull String text) {
        this.text = text;
    }

    public String asString() {
        return this.text;
    }

    public char getDomain() {
        return text.charAt(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ResourceId resourceId = (ResourceId) o;
        return text.equals(resourceId.text);
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }

    @Override
    public String toString() {
        return text;
    }

}
