package org.activityinfo.ui.vdom.shared.tree;

import org.activityinfo.ui.vdom.shared.html.HasClassNames;

import java.util.HashMap;
import java.util.Map;

/**
 * Node Property Map
 */
public class PropMap {

    private Map<String, Object> propMap = new HashMap<>();

    public static boolean isObject(Object object) {
        return object instanceof PropMap;
    }

    public PropMap set(String propName, Object object) {
        if(!(object == null || object instanceof PropMap || object instanceof String)) {
            throw new IllegalArgumentException("object must be a PropMap or String. Was: " + object);
        }
        propMap.put(propName, object);
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

    public static PropMap withClasses(String classes) {
        PropMap propMap = new PropMap();
        propMap.set("className", classes);
        return propMap;
    }
    public static PropMap withClasses(HasClassNames classNames) {
        return withClasses(classNames.getClassNames());
    }
    public boolean isEmpty() {
        return propMap.isEmpty();
    }


}
