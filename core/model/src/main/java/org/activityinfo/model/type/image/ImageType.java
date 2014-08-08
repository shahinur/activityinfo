package org.activityinfo.model.type.image;
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
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.RecordFieldTypeClass;
import org.activityinfo.model.type.component.ComponentReader;
import org.activityinfo.model.type.component.NullComponentReader;

/**
 * @author yuriyz on 8/6/14.
 */
public class ImageType implements FieldType {

    public static class TypeClass implements RecordFieldTypeClass {

        private TypeClass() {
        }

        @Override
        public String getId() {
            return "IMAGE";
        }

        @Override
        public String getLabel() {
            return "Image";
        }

        @Override
        public FieldType createType() {
            return new ImageType();
        }

        @Override
        public FieldValue deserialize(Record record) {
            return ImageValue.fromRecord(record);
        }
    }

    public static final TypeClass TYPE_CLASS = new TypeClass();

    public ImageType() {
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return TYPE_CLASS;
    }

    @Override
    public ComponentReader<String> getStringReader(String fieldName, String componentId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ComponentReader<LocalDate> getDateReader(String name, String componentId) {
        return new NullComponentReader<>();
    }

    @Override
    public String toString() {
        return "ImageType";
    }
}