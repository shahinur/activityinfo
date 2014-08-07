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
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceIdPrefixType;
import org.activityinfo.model.type.*;
import org.activityinfo.model.type.component.ComponentReader;
import org.activityinfo.model.type.component.NullComponentReader;
import org.activityinfo.model.type.number.QuantityType;

/**
 * @author yuriyz on 8/6/14.
 */
public class ImageType implements ParametrizedFieldType {

    public static class TypeClass implements ParametrizedFieldTypeClass, RecordFieldTypeClass {

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
        public ImageType deserializeType(Record typeParameters) {
            return new ImageType();
        }

        @Override
        public FormClass getParameterFormClass() {
            FormClass formClass = new FormClass(ResourceIdPrefixType.TYPE.id("image"));
            formClass.addElement(new FormField(ResourceId.create("mimeType"))
                    .setType(FREE_TEXT.createType())
                    .setLabel("Mime type")
                    .setDescription("Mime type of image (e.g. image/gif, image/jpeg, image/png)")
                    .setRequired(true)
            );
            formClass.addElement(new FormField(ResourceId.create("filename"))
                    .setType(FREE_TEXT.createType())
                    .setLabel("File name")
                    .setDescription("Name of image file")
                    .setRequired(false)
            );
            formClass.addElement(new FormField(ResourceId.create("token"))
                    .setType(FREE_TEXT.createType())
                    .setLabel("Token")
                    .setDescription("Token which is used to retrieve the image file.")
                    .setVisible(false)
            );
            formClass.addElement(new FormField(ResourceId.create("width"))
                    .setType(QuantityType.TYPE_CLASS.createType().setUnits("pixels"))
                    .setLabel("Width")
                    .setDescription("Width of image")
            );
            formClass.addElement(new FormField(ResourceId.create("height"))
                    .setType(QuantityType.TYPE_CLASS.createType().setUnits("pixels"))
                    .setLabel("Height")
                    .setDescription("Height of image")
            );
            return formClass;
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
    public ParametrizedFieldTypeClass getTypeClass() {
        return TYPE_CLASS;
    }

    @Override
    public Record getParameters() {
        return new Record().
                set("classId", getTypeClass().getParameterFormClass().getId());
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