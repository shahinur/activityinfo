package org.activityinfo.model.resource;


import com.fasterxml.jackson.annotation.JsonValue;
import com.google.gwt.core.client.JavaScriptObject;

import javax.annotation.Nonnull;
import java.util.Date;

/**
 * This is an alternate implementation of ResourceId used by GWT
 * at compile time, that effectively replaces the ResourceId with a
 * normal Javascript string.
 */
public final class ResourceId extends JavaScriptObject {

    public static final ResourceId ROOT_ID = ResourceId.valueOf("_root");

    public static final int RADIX = 10;
    public static long COUNTER = 1;

    protected ResourceId() {}

    /**
     * Creates a new ResourceId from its string representation
     *
     * <p>Note: This method must be named {@code valueOf} in order to be
     * used as a Jersey {@code @PathParam}
     */
    public static native ResourceId valueOf(@Nonnull String string) /*-{
      return string;
    }-*/;

    public static ResourceId generateId() {
        return valueOf("c" + Long.toString(new Date().getTime(), Character.MAX_RADIX) +
                       Long.toString(COUNTER++, Character.MAX_RADIX));
    }


    @JsonValue
    public native String asString() /*-{
        return this;
    }-*/;

    public char getDomain() {
        return asString().charAt(0);
    }
}
