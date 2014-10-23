package org.activityinfo.server.command.handler.adapter;

import com.extjs.gxt.ui.client.data.ModelData;

/**
 * Checks and converts properties to a given type.
 *
 * <p>N.B., normally we could use {@link Class#cast(Object)} but we need something static
 * we can translate to JS via GWT.
 *
 */
public interface PropertyType<T> {

    /**
     *
     * @return a human-readable description of the property type.
     */
    String name();

    /**
     * Casts {@code value} to type {@code T}
     * @param value the value to convert
     * @return value cast to {@code T}
     * @throws java.lang.ClassCastException
     */
    T cast(Object value);

    public static final PropertyType<String> STRING = new PropertyType<String>() {
        @Override
        public String name() {
            return "string";
        }

        @Override
        public String cast(Object value) {
            return ((String)value).trim();
        }
    };

    public static final PropertyType<Integer> INTEGER = new PropertyType<Integer>() {
        @Override
        public String name() {
            return "integer";
        }

        @Override
        public Integer cast(Object value) {
            return ((Number)value).intValue();
        }
    };


    public static final PropertyType<Boolean> BOOLEAN = new PropertyType<Boolean>() {
        @Override
        public String name() {
            return "boolean";
        }

        @Override
        public Boolean cast(Object value) {
            return (Boolean) value;
        }
    };

    public static final PropertyType<PropertyMap> MODEL = new PropertyType<PropertyMap>() {
        @Override
        public String name() {
            return "object";
        }

        @Override
        public PropertyMap cast(Object value) {
            ModelData modelData = (ModelData) value;
            return new PropertyMap(modelData.getProperties());
        }
    };
}
