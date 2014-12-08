package org.activityinfo.model.type.subform;
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

import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceIdPrefixType;
import org.activityinfo.model.type.*;
import org.activityinfo.model.type.number.Quantity;

/**
 * @author yuriyz on 12/03/2014.
 */
public class SubFormType implements ParametrizedFieldType {


    public static class TypeClass implements ParametrizedFieldTypeClass, RecordFieldTypeClass {

        private TypeClass() {}

        @Override
        public String getId() {
            return "SUBFORM";
        }

        @Override
        public SubFormType createType() {
            return new SubFormType();
        }

        @Override
        public SubFormType deserializeType(Record typeParameters) {
            return new SubFormType()
                    .setClassReference((ReferenceType) ReferenceType.TYPE_CLASS.deserializeType(typeParameters.getRecord("classReference")))
                    .setDataOwnerClass(ResourceId.valueOf(typeParameters.getString("dataOwnerClass")));
        }

        @Override
        public FormClass getParameterFormClass() {
            return new FormClass(ResourceIdPrefixType.TYPE.id("subform"));
        }

        @Override
        public FieldValue deserialize(Record record) {
            return Quantity.fromRecord(record);
        }
    }

    public static final TypeClass TYPE_CLASS = new TypeClass();

    private ReferenceType classReference;
    private ResourceId dataOwnerClass; // refers to owner FormClass

    public SubFormType() {
    }

    public SubFormType(ReferenceType classReference, ResourceId subFormOf) {
        this.classReference = classReference;
        this.dataOwnerClass = subFormOf;
    }

    public ReferenceType getClassReference() {
        return classReference;
    }

    public SubFormType setClassReference(ReferenceType classReference) {
        this.classReference = classReference;
        return this;
    }

    public ResourceId getDataOwnerClass() {
        return dataOwnerClass;
    }

    public SubFormType setDataOwnerClass(ResourceId dataOwnerClass) {
        this.dataOwnerClass = dataOwnerClass;
        return this;
    }

    @Override
    public ParametrizedFieldTypeClass getTypeClass() {
        return TYPE_CLASS;
    }

    @Override
    public Record getParameters() {
        return new Record()
                .set("classId", getTypeClass().getParameterFormClass().getId())
                .set("classReference", classReference.getParameters())
                .set("dataOwnerClass", dataOwnerClass.asString());
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public String toString() {
        return "SubFormType";
    }
}
