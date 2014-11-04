package org.activityinfo.model.resource;

import com.google.gwt.core.client.JavaScriptObject;

import java.util.Date;

public final class ResourceId extends JavaScriptObject {

    public static long COUNTER = 1;

    public static native ResourceId create(String string) /*-{
        return string;
    }-*/;

    public static ResourceId generateId() {
        return create("c" + Long.toString(new Date().getTime(), Character.MAX_RADIX) +
                      Long.toString(COUNTER++, Character.MAX_RADIX));
    }

    public native String asString() /*- {
        return this;
    }-*/;

    public char getDomain() {
        return asString().charAt(0);
    }
}
