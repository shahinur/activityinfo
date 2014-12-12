package org.activityinfo.model.form;
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
import org.activityinfo.model.record.Record;
import org.activityinfo.model.record.RecordBuilder;
import org.activityinfo.model.record.Records;
import org.activityinfo.model.resource.IsResource;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.ReferenceValue;
import org.activityinfo.model.type.TypeRegistry;
import org.activityinfo.model.type.enumerated.EnumValue;
import org.activityinfo.model.type.geo.AiLatLng;
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
 * TODO Either rewrite this class so that it has reference semantics, or make it clear that it does not!
 *
 * @author yuriyz on 1/29/14.
 */
public class FormInstance implements IsResource {
    final private ResourceId id;
    private ResourceId ownerId;
    private Record record;

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
        this.ownerId = classId;
        this.record = Records.builder(classId).build();
    }

    @Override
    public ResourceId getId() {
        return id;
    }

    public static FormInstance fromResource(Resource resource) {
        FormInstance instance = new FormInstance(resource.getId(), resource.getValue().getClassId());
        if (resource.getOwnerId() != null) { // owner may be null for FieldTypes
            instance.setOwnerId(resource.getOwnerId());
        }
        instance.setAll(resource.getValue());
        return instance;
    }

    @Override
    public Resource asResource() {
        Resource resource = Resources.createResource(record);
        resource.setId(id);
        resource.setOwnerId(ownerId);
        return resource;
    }

    public ResourceId getClassId() {
        return record.getClassId();
    }

    public FormInstance setOwnerId(ResourceId ownerId) {
        assert ownerId != null;
        this.ownerId = ownerId;
        return this;
    }

    public ResourceId getOwnerId() {
        return ownerId;
    }

    public Map<ResourceId, Object> getValueMap() {
        Map<ResourceId, Object> valueMap = Maps.newHashMap();
        for(Object key : record.asMap().keySet()) {
            String fieldName = (String)key;
            ResourceId fieldId = ResourceId.valueOf(fieldName);
            Object value = record.get(fieldName);

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
        for(Object key : record.asMap().keySet()) {
            ResourceId fieldId = ResourceId.valueOf((String) key);
            valueMap.put(fieldId, get(fieldId));
        }
        return valueMap;
    }

    public void removeAll(Set<ResourceId> fieldIds) {
        RecordBuilder recordBuilder = Records.buildCopyOf(this.record);

        for (ResourceId fieldId : fieldIds) {
            recordBuilder.set(fieldId.asString(), (String) null);
        }

        this.record = recordBuilder.build();
    }

    public FormInstance set(@NotNull ResourceId fieldId, ResourceId referenceId) {
        return set(fieldId, new ReferenceValue(referenceId));
    }

    public FormInstance set(@NotNull ResourceId fieldId, String value) {
        this.record = Records.buildCopyOf(this.record).set(fieldId.asString(), value).build();
        return this;
    }

    public FormInstance set(@NotNull ResourceId fieldId, double value) {
        this.record = Records.buildCopyOf(this.record).set(fieldId.asString(), value).build();
        return this;
    }

    public FormInstance set(@NotNull ResourceId fieldId, boolean value) {
        this.record = Records.buildCopyOf(this.record).set(fieldId.asString(), value).build();
        return this;
    }

    public FormInstance set(@NotNull ResourceId fieldId, FieldValue fieldValue) {
        this.record = Records.buildCopyOf(this.record).setFieldValue(fieldId.asString(), fieldValue).build();
        return this;
    }

    public void set(@NotNull ResourceId fieldId, Set<ResourceId> references) {
        set(fieldId, new ReferenceValue(references));
    }

    public void set(@NotNull ResourceId fieldId, Date date) {
        set(fieldId, new LocalDate(date));
    }

    public void set(@NotNull ResourceId fieldId, com.bedatadriven.rebar.time.calendar.LocalDate rebarDate) {
        set(fieldId, LocalDate.valueOf(rebarDate));
    }

    public void set(@NotNull ResourceId fieldId, AiLatLng aiLatLng) {
        set(fieldId, new GeoPoint(aiLatLng));
    }

    public void set(ResourceId fieldId, Object value) {
        if (value instanceof ResourceId) {
            set(fieldId, (ResourceId) value);
        } else if (value instanceof String) {
            set(fieldId, (String) value);
        } else if (value instanceof Double) {
            set(fieldId, ((Double) value).doubleValue());
        } else if (value instanceof Boolean) {
            set(fieldId, ((Boolean) value).booleanValue());
        } else if (value instanceof FieldValue) {
            set(fieldId, (FieldValue) value);
        } else if (value instanceof Set) {
            set(fieldId, (Set) value);
        } else if (value instanceof Date) {
            set(fieldId, (Date) value);
        } else if (value instanceof com.bedatadriven.rebar.time.calendar.LocalDate) {
            set(fieldId, (com.bedatadriven.rebar.time.calendar.LocalDate) value);
        } else if (value instanceof AiLatLng) {
            set(fieldId, (AiLatLng) value);
        } else if (value != null) {
            throw new IllegalArgumentException(value.getClass().toString());
        }
    }

    public FieldValue get(ResourceId fieldId) {
        Object value = record.get(fieldId.asString());

        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return TextValue.valueOf((String) value);
        } else if (value instanceof Boolean) {
            Boolean booleanValue = (Boolean) value;
            return BooleanFieldValue.valueOf(booleanValue);
        } else if (value instanceof Record) {
            Record record = (Record) value;

            try {
                return TypeRegistry.get().deserializeFieldValue(record);
            } catch (Exception e) {
                throw new RuntimeException("Exception thrown while reading property '" + fieldId + "' from " + value);
            }
        } else if (value instanceof Double) {
            return new Quantity((Double) value);
        } else {
            throw new UnsupportedOperationException(fieldId + " = " + value);
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
        return record.isString(fieldId.asString());
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
        } else if(value instanceof EnumValue) {
            return ((EnumValue) value).getResourceIds();
        }else {
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
        copy.setOwnerId(getOwnerId());
        copy.setAll(record);
        return copy;
    }

    @Override
    public String toString() {
        return "FormInstance{" +
                "id=" + id +
                ", record=" + record +
                '}';
    }

    private void setAll(Record record) {
        this.record = Records.buildCopyOf(this.record).setAll(record).build();
    }
}
