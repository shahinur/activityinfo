package org.activityinfo.model.resource;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Base class for Resource and Record
 */
class PropertyBag<T extends PropertyBag> {

    private final Map<String, Object> properties = Maps.newHashMap();

    /**
     *
     * @return the value of the given property, or {@code null} if there
     * is no value for this property.
     */
    public Object get(String propertyName) {
        return properties.get(propertyName);
    }

    /**
     * @return true if there is a value for the given property
     */
    public boolean has(String propertyName) {
        return properties.containsKey(propertyName);
    }


    /**
     * @return the value of the given boolean property, or the {@code defaultValue}
     * if there is no value for this property or the value is not of Boolean type.
     */
    public boolean getBoolean(String propertyName, boolean defaultValue) {
        Object value = properties.get(propertyName);
        if(value == Boolean.TRUE) {
            return true;
        } else if(value == Boolean.FALSE) {
            return false;
        } else {
            return defaultValue;
        }
    }

    /**
     *
     * @return the field's value as an immutable list of {@code Record}s.
     * If the field has no value, or the value is not of the array type,
     * an empty list is returned.
     */
    @Nonnull
    public List<Record> getRecordList(String propertyName) {
        Object value = properties.get(propertyName);
        if(value instanceof List) {
            return (List)value;
        }
        return Collections.emptyList();
    }

    /**
     * @return the value of the given property as a String
     * @throws java.lang.ClassCastException if the value of the property is not a string
     * @throws java.lang.NullPointerException if there is no value for the property
     */
    @Nonnull
    public String getString(String propertyName) {
        String value = (String) properties.get(propertyName);
        if(value == null) {
            throw new NullPointerException(propertyName + " is not present in " + this);
        }
        return value;
    }

    public String isString(String propertyName) {
        Object value = properties.get(propertyName);
        if(value instanceof String) {
            return (String)value;
        }
        return null;
    }

    /**
     * @return the value of the reference property
     * @throws java.lang.ClassCastException if the value of the property is not a Reference
     * @throws java.lang.NullPointerException if there is no value for the property
     */
    public Reference getReference(String propertyName) {
        Reference reference = (Reference) properties.get(propertyName);
        if(reference == null) {
            throw new NullPointerException(propertyName);
        }
        return reference;
    }

    /**
     * @return an immutable set of references defined for the given property, or an
     * empty set if the property is absent or not reference type.
     */
    public Set<Reference> getReferenceSet(String propertyName) {
        Object value = properties.get(propertyName);
        if(value instanceof Reference) {
            return Collections.singleton((Reference) value);
        } else if(value instanceof Iterable) {
            Set<Reference> refs = Sets.newHashSet();
            for(Object item : (Iterable)value) {
                if(item instanceof Reference) {
                    refs.add((Reference) item);
                }
            }
            return refs;
        } else {
            return Collections.emptySet();
        }
    }

    /**
     * @return this field's value as a {@code Record}
     *
     */
    @Nonnull
    public Record getRecord(String propertyName) {
        Record value = (Record) properties.get(propertyName);
        if(value == null) {
            throw new NullPointerException(propertyName);
        }
        return value;
    }

    public Record isRecord(String propertyName) {
        Object value = properties.get(propertyName);
        if(value instanceof Record) {
            return (Record) value;
        }
        return null;
    }

    /**
     * @return the value of this field as a {@code double}
     * @throws java.lang.ClassCastException if the value of the property is not a Number
     * @throws java.lang.NullPointerException if there is no value for the property
     */
    public double getDouble(String propertyName) {
        Number value = (Number)properties.get(propertyName);
        if(value == null) {
            throw new NullPointerException(propertyName);
        }
        return value.doubleValue();
    }

    /**
     * Sets the named property to the given {@code record}, or
     * removes the property if {@code record} is {@code null}.
     */
    public T set(String propertyName, Record record) {
        if(record == null) {
            properties.remove(propertyName);
        } else {
            properties.put(propertyName, record);
        }
        return (T)this;
    }

    /**
     * Sets the named property to the given {@code string}, or
     * removes the property if {@code string} is {@code null} or
     * empty.
     */
    public T set(String propertyName, String string) {
        if (Strings.isNullOrEmpty(string)) {
            properties.remove(propertyName);
        } else {
            properties.put(propertyName, string);
        }
        return (T)this;
    }

    /**
     * Sets the named property to the given {@code recordList}, or
     * removes the property if {@code recordList} is {@code null} or
     * empty.
     */
    public T set(String propertyName, List<Record> recordList) {
        if (recordList == null || recordList.isEmpty()) {
            properties.remove(propertyName);
        } else {
            properties.put(propertyName, recordList);
        }
        return (T)this;
    }

    /**
     * Sets the named property to the given {@code doubleValue}
     */
    public T set(String propertyName, double doubleValue) {
        properties.put(propertyName, doubleValue);
        return (T)this;
    }

    public T set(String propertyName, ResourceId id) {
        if(id == null) {
            properties.remove(propertyName);
        } else {
            properties.put(propertyName, Reference.to(id));
        }
        return (T)this;
    }

    /**
     * Sets the named property to the name of the given
     * {@code enumValue}, or removes the property if
     * {@code enumValue} is {@code null}.
     *
     * Equivalent to calling:
     * <blockquote>
     * {@code set(propertyName, enumValue == null ? null : enumValue.name()); }
     * </blockquote>
     */
    public T set(String propertyName, Enum<?> enumValue) {
        if(enumValue == null) {
            properties.remove(propertyName);
        } else {
            properties.put(propertyName, enumValue.name());
        }
        return (T)this;
    }

    /**
     * Sets the named property to the given value,
     * or removes the property if the {@code value} is null.
     *
     * @throws java.lang.IllegalArgumentException if {@code value} is not an instance of
     * <ul>
     *     <li>{@code java.lang.String}</li>
     *     <li>{@code java.lang.Number}</li>
     *     <li>{@code java.lang.Boolean}</li>
     *     <li>{@code Reference}</li>
     *     <li>{@code Record}</li>
     *     <li>{@code java.util.List}</li>
     * </ul>
     */
    public T set(String propertyName, Object value) {
        if(value == null) {
            properties.remove(propertyName);

        } else if(value instanceof String ||
                  value instanceof Number ||
                  value instanceof Record ||
                  value instanceof Boolean ||
                  value instanceof Reference ||
                  value instanceof List) {

           properties.put(propertyName, value);

        } else {
            throw new IllegalArgumentException("Invalid field type: " + value.getClass().getName());
        }
        return (T)this;
    }

    /**
     * Sets the named property to the name of the given
     * {@code booleanValue}
     */
    public T set(String propertyName, boolean booleanValue) {
        properties.put(propertyName, booleanValue);
        return (T)this;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PropertyBag that = (PropertyBag) o;

        return this.getProperties().equals(that.properties);
    }

    @Override
    public int hashCode() {
        return properties.hashCode();
    }

}
