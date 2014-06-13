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

import com.bedatadriven.rebar.time.calendar.LocalDate;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.Resource;
import org.activityinfo.core.shared.model.AiLatLng;
import org.activityinfo.legacy.shared.model.DTO;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @author yuriyz on 1/29/14.
 */
public class FormInstance implements Resource {

    private Cuid id;
    private Cuid classId;
    private final Map<Cuid, Object> valueMap = Maps.newHashMap();
    private Cuid parentId;

    /**
     * Constructs a new FormInstance. To obtain an id for a new instance
     * use
     *
     * @param id the id of the instance.
     * @param classId the id of this form's class
     */
    public FormInstance(@Nonnull Cuid id, @Nonnull Cuid classId) {
        Preconditions.checkNotNull(id, classId);
        this.id = id;
        this.classId = classId;
    }

    @Override
    public Cuid getId() {
        return id;
    }

    public Cuid getClassId() {
        return classId;
    }

    public void setParentId(Cuid parentId) {
        this.parentId = parentId;
    }

    public Cuid getParentId() {
        return parentId;
    }

    public Map<Cuid, Object> getValueMap() {
        return valueMap;
    }

    public void removeAll(Set<Cuid> fieldIds) {
        for (Cuid fieldId : fieldIds) {
            valueMap.remove(fieldId);
        }
    }

    public void set(@NotNull Cuid fieldId, Object fieldValue) {
        Preconditions.checkNotNull(fieldId);
        if (fieldValue instanceof LocalDate) {
            // not sure if we want to use LocalDate or Date here -- may only matter at the moment
            // of serialization
            fieldValue = ((LocalDate) fieldValue).atMidnightInMyTimezone();
        }
        if (fieldValue instanceof DTO) {
            throw new IllegalArgumentException("Please use cuid reference instead of legacy class.");
        }

        valueMap.put(fieldId, fieldValue);
    }

    public Object get(Cuid fieldId) {
        return valueMap.get(fieldId);
    }

    public String getString(Cuid fieldId) {
        final Object value = get(fieldId);
        if (value instanceof String) {
            return (String) value;
        }
        return null;
    }

    public LocalDate getDate(Cuid fieldId) {
        final Object value = get(fieldId);
        if (value instanceof Date) {
            return (LocalDate) value;
        }
        return null;
    }

    public Cuid getInstanceId(Cuid fieldId) {
        final Object value = get(fieldId);
        if(value instanceof Cuid) {
            return (Cuid) value;
        }
        return null;
    }


    public Set<Cuid> getReferences(Cuid fieldId) {
        final Object value = get(fieldId);
        if(value instanceof Cuid) {
            return Collections.singleton((Cuid)value);
        } else if(value instanceof Set) {
            return (Set<Cuid>)value;
        }
        return Sets.newHashSet();
    }

    public Double getDouble(Cuid fieldId) {
        final Object value = get(fieldId);
        if (value instanceof Double) {
            return (Double) value;
        }
        return null;
    }

    public FormInstance copy() {
        final FormInstance copy = new FormInstance(getId(), getClassId());
        copy.getValueMap().putAll(this.getValueMap());
        return copy;
    }

    public AiLatLng getPoint(Cuid fieldId) {
        final Object value = get(fieldId);
        if (value instanceof AiLatLng) {
            return (AiLatLng) value;
        }
        return null;
    }

    @Override
    public String toString() {
        return "FormInstance{" +
                "id=" + id +
                ", classId=" + classId +
                ", valueMap=" + valueMap +
                '}';
    }

}
