package org.activityinfo.ui.vdom.shared.tree;

import com.google.common.base.Joiner;
import org.activityinfo.ui.vdom.shared.html.AriaRole;
import org.activityinfo.ui.vdom.shared.html.HasClassNames;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Node Property Map
 */
public class PropMap {

    public static final PropMap EMPTY = empty();

    private Map<String, Object> propMap = new HashMap<>();

    /**
     * Creates a new {@code PropMap} with the given style object.
     */
    public static PropMap withStyle(Style style) {
        PropMap propMap = new PropMap();
        propMap.setStyle(style);
        return propMap;
    }

    /**
     * Creates a new {@code PropMap} with the given value for the {@code className} property
     */
    public static PropMap withClasses(String classes) {
        PropMap propMap = new PropMap();
        propMap.set("className", classes);
        return propMap;
    }

    /**
     * Creates a new {@code PropMap} with the given value for the {@code className} property
     */
    public static PropMap withClasses(HasClassNames classNames) {
        return withClasses(classNames.getClassNames());
    }


    /**
     * Sets the "aria-hidden" property to true"
     */
    public PropMap ariaHidden() {
        return set("aria-hidden", "true");
    }

    /**
     * Sets the "role" property to the given Aria role
     */
    public PropMap role(AriaRole role) {
        return set("role", role.name().toLowerCase());
    }

    /**
     * Sets the data-{dataPropertyName} property to the given value.
     */
    public PropMap data(String dataPropertyName, String value) {
        return set("data-" + dataPropertyName, value);
    }


    /**
     * Sets the "id" property
     */
    public PropMap setId(String id) {
        return set("id", id);
    }


    public PropMap setClass(HasClassNames classNames) {
        return set("className", classNames.getClassNames());
    }

    public static boolean isObject(Object object) {
        return object instanceof PropMap;
    }

    public PropMap setStyle(Style style) {
        propMap.put("style", style.asPropMap());
        return this;
    }

    public PropMap set(String propName, String value) {
        propMap.put(propName, value);
        return this;
    }

    public Iterable<String> keys() {
        return propMap.keySet();
    }

    public Object get(String propName) {
        return propMap.get(propName);
    }

    public boolean contains(String key) {
        return propMap.keySet().contains(key);
    }

    public Iterable<Map.Entry<String, Object>> entrySet() {
        return propMap.entrySet();
    }

    public boolean isEmpty() {
        return propMap.isEmpty();
    }

    public void trustedSet(String key, Object value) {
        propMap.put(key, value);
    }

    private static PropMap empty() {
        PropMap propMap = new PropMap();
        propMap.propMap = Collections.emptyMap();
        return propMap;
    }


    @Override
    public String toString() {
        return Joiner.on(" ").withKeyValueSeparator("=").join(propMap);
    }

}
