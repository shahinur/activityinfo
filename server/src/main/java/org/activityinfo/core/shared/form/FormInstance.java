package org.activityinfo.core.shared.form;
/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.activityinfo.core.shared.model.AiLatLng;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.ReferenceValue;
import org.activityinfo.model.type.TypeRegistry;
import org.activityinfo.model.type.geo.GeoPoint;
import org.activityinfo.model.type.number.Quantity;
import org.activityinfo.model.type.primitive.BooleanFieldValue;
import org.activityinfo.model.type.primitive.TextValue;
import org.activityinfo.model.type.time.LocalDate;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 *
 * Wrapper for a {@code Record} or {@code Resource} that exposes its properties
 * as {@code FieldValue}s
 *
 * @author yuriyz on 1/29/14.
 */
public class FormInstance implements IsResource {

    private ResourceId id;
    private ResourceId classId;
    private ResourceId parentId;
    private PropertyBag propertyBag;

    /**
     * Constructs a new FormInstance. To obtain an id for a new instance
     * use
     *
     * @param id the id of the instance.
     * @param classId the id of this form's class
     */
    public FormInstance(@Nonnull ResourceId id, @Nonnull ResourceId classId) {
        Preconditions.checkNotNull(id, classId);
        this.id = id;
        this.classId = classId;
        this.propertyBag = new PropertyBag();
    }

    @Override
    public ResourceId getId() {
        return id;
    }

    public static FormInstance fromResource(Resource resource) {
        FormInstance instance = new FormInstance(resource.getId(), resource.getResourceId("classId"));
        instance.propertyBag.setAll(resource);
        return instance;
    }

    @Override
    public Resource asResource() {
        Resource resource = Resources.createResource();
        resource.setId(id);
        if (parentId != null) {
            resource.setOwnerId(parentId);
        }
        resource.set("classId", classId);
        resource.setAll(propertyBag);
        return resource;
    }

    public ResourceId getClassId() {
        return classId;
    }

    public void setOwnerId(ResourceId parentId) {
        this.parentId = parentId;
    }

    public ResourceId getParentId() {
        return parentId;
    }

    public Map<ResourceId, Object> getValueMap() {
        Map<ResourceId, Object> valueMap = Maps.newHashMap();
        for(Object key : propertyBag.getProperties().keySet()) {
            String fieldName = (String)key;
            ResourceId fieldId = ResourceId.create(fieldName);
            Object value = propertyBag.get(fieldName);

            if(value instanceof String) {
                valueMap.put(fieldId, value);
            } else if(value instanceof Record) {
                valueMap.put(fieldId,
                        TypeRegistry.get().deserializeFieldValue((Record)value));
            } else {
                throw new UnsupportedOperationException("value: " + value);
            }
        }
        return Collections.unmodifiableMap(valueMap);
    }

    public Map<ResourceId, FieldValue> getFieldValueMap() {
        Map<ResourceId, FieldValue> valueMap = Maps.newHashMap();
        for(Object key : propertyBag.getProperties().keySet()) {
            ResourceId fieldId = ResourceId.create((String)key);
            valueMap.put(fieldId, get(fieldId));
        }
        return valueMap;
    }

    public void removeAll(Set<ResourceId> fieldIds) {
        for (ResourceId fieldId : fieldIds) {
            propertyBag.remove(fieldId.asString());
        }
    }

    public void set(@NotNull ResourceId fieldId, Object fieldValue) {
        Preconditions.checkNotNull(fieldId);
        if (fieldValue == null) {
            propertyBag.remove(fieldId.asString());

        } else if (fieldValue instanceof ResourceId) {
            // TODO(alex) once rest of code has been updated
            propertyBag.set(fieldId.asString(), new ReferenceValue((ResourceId)fieldValue).toRecord());

        } else if (fieldValue instanceof Date) {
            // TODO(alex) once rest of code has been updated
            propertyBag.set(fieldId.asString(), new LocalDate((Date)fieldValue).toRecord());

        } else if (fieldValue instanceof Number) {
            // TODO(alex) once rest of code has been updated

            Quantity quantity = new Quantity(((Number) fieldValue).doubleValue(), null);
            propertyBag.set(fieldId.asString(), quantity.toRecord());

        } else if (fieldValue instanceof String) {
            propertyBag.set(fieldId.asString(), (String) fieldValue);

        } else if (fieldValue instanceof TextValue) {
            propertyBag.set(fieldId.asString(), ((TextValue) fieldValue).toString());

        } else if (fieldValue instanceof BooleanFieldValue) {
            propertyBag.set(fieldId.asString(), fieldValue == BooleanFieldValue.TRUE);

        } else if(fieldValue instanceof Boolean) {
            propertyBag.set(fieldId.asString(), (Boolean)fieldValue);

        } else if(fieldValue instanceof IsRecord) {
            propertyBag.set(fieldId.asString(), ((IsRecord) fieldValue).toRecord());

        } else {
            throw new UnsupportedOperationException(fieldId + " = " + fieldValue);
        }
    }

    public FieldValue get(ResourceId fieldId) {
        Object value = propertyBag.get(fieldId.asString());
        if(value == null) {
            return null;
        } else if(value instanceof String) {
            return TextValue.valueOf((String) value);
        } else if(value instanceof Boolean) {
            Boolean booleanValue = (Boolean) value;
            return BooleanFieldValue.valueOf(booleanValue);
        } else if(value instanceof Record) {
            Record record = (Record)value;
            return TypeRegistry.get().deserializeFieldValue(record);
        } else {
            throw new UnsupportedOperationException(fieldId.asString() + " = " + value);
        }
    }

    /**
     * Returns the value of {@code fieldId} if the value is present and of
     * the specified {@code typeClass}, or {@code null} otherwise.
     */
    public FieldValue get(ResourceId fieldId, FieldTypeClass typeClass) {
        FieldValue value = get(fieldId);
        if(value.getTypeClass() == typeClass) {
            return value;
        } else {
            return null;
        }
    }

    public ResourceId getInstanceId(ResourceId fieldId) {
        final FieldValue value = get(fieldId);
        if(value instanceof ReferenceValue) {
            return ((ReferenceValue) value).getResourceIds().iterator().next();
        }
        return null;
    }

    public String getString(ResourceId fieldId) {
        return propertyBag.isString(fieldId.asString());
    }

    public LocalDate getDate(ResourceId fieldId) {
        final Object value = get(fieldId);
        if (value instanceof LocalDate) {
            return (LocalDate) value;
        }
        return null;
    }

    public Set<ResourceId> getReferences(ResourceId fieldId) {
        FieldValue value = get(fieldId);
        if(value instanceof ReferenceValue) {
            return ((ReferenceValue) value).getResourceIds();
        } else {
            return Collections.emptySet();
        }
    }

    public Double getDouble(ResourceId fieldId) {
        FieldValue value = get(fieldId);
        if(value instanceof Quantity) {
            return ((Quantity) value).getValue();
        }
        return null;
    }

    public FormInstance copy() {
        final FormInstance copy = new FormInstance(getId(), getClassId());
        copy.propertyBag.setAll(propertyBag);
        return copy;
    }

    public AiLatLng getPoint(ResourceId fieldId) {
        FieldValue value = get(fieldId);
        if(value instanceof GeoPoint) {
            GeoPoint geoPoint = (GeoPoint) value;
            return new AiLatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
        }
        return null;
    }

    @Override
    public String toString() {
        return "FormInstance{" +
                "id=" + id +
                ", classId=" + classId +
                ", valueMap=" + propertyBag +
                '}';
    }

}
