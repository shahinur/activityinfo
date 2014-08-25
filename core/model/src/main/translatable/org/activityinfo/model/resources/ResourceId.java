package org.activityinfo.model.resource;


import com.fasterxml.jackson.annotation.JsonValue;
import com.google.gwt.core.client.JavaScriptObject;

import javax.annotation.Nonnull;

/**
 * Globally, universally unique and persistent identifier
 * for {@code Resources}
 *
 */
public final class ResourceId extends JavaScriptObject {

    protected ResourceId() { }

    /**
     * Creates a new ResourceId from its string representation
     *
     * <p>Note: This method must be named {@code valueOf} in order to be
     * used as a Jersey {@code @PathParam}
     */
    public static ResourceId valueOf(@Nonnull String string) /*-{
        return string;
    }-*/;

    public char getDomain() {
        return toString().charAt(0);
    }
}
