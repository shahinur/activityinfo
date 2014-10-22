package org.activityinfo.server.command.handler.adapter;

import com.extjs.gxt.ui.client.data.RpcMap;

import java.util.Map;

/**
 * Interface to list of named properties that
 * performs basic validation on the content/type
 */
public class PropertyMap {

    private Map<String, Object> map;

    public PropertyMap(Map<String, Object> map) {
        this.map = map;
    }

    public PropertyMap(RpcMap properties) {
        this.map = properties.getTransientMap();
    }

    /**
     * @param name the property name
     * @return the value of the property named {@code name}
     * @throws org.activityinfo.server.command.handler.adapter.PropertyException if the property is missing or not
     *                                                                           of type String
     */
    String getString(String name) {
        return get(name, PropertyType.STRING);
    }

    public int getInt(String name) {
        return get(name, PropertyType.INTEGER);
    }

    public PropertyMap getModel(String name) {
        return get(name, PropertyType.MODEL);
    }

    private <T> T get(String name, PropertyType<T> propertyType) {
        Object value = map.get(name);
        if(value == null) {
            throw PropertyException.missing(name);
        }
        try {
            return propertyType.cast(value);
        } catch (ClassCastException e) {
            throw PropertyException.invalidType(name, propertyType.name(), value);
        }
    }


    public boolean contains(String propertyName) {
        return map.containsKey(propertyName);
    }
}
