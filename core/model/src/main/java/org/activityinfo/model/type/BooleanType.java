package org.activityinfo.model.type;
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
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceIdPrefixType;
import org.activityinfo.model.type.component.ComponentReader;
import org.activityinfo.model.type.component.NullComponentReader;

/**
 * @author yuriyz on 7/21/14.
 */
public enum BooleanType implements FieldType, FieldTypeClass {

    INSTANCE;

    @Override
    public String getId() {
        return "BOOLEAN";
    }

    @Override
    public String getLabel() {
        return "Boolean";
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return this;
    }

    @Override
    public Record getParameters() {
        return new Record().set("classId", getTypeClass().getParameterFormClass().getId());
    }

    @Override
    public ComponentReader getStringReader(final String fieldName, String componentId) {
        assert DEFAULT_COMPONENT.equals(componentId);
        return new ComponentReader() {
            @Override
            public String read(Resource resource) {
                return resource.isString(fieldName);
            }
        };
    }

    @Override
    public ComponentReader<LocalDate> getDateReader(String name, String componentId) {
        return new NullComponentReader<>();
    }


    @Override
    public FieldType createType(Record typeParameters) {
        return this;
    }

    @Override
    public FieldType createType() {
        return this;
    }


    @Override
    public FormClass getParameterFormClass() {
        return new FormClass(ResourceIdPrefixType.TYPE.id("boolean"));
    }
}
