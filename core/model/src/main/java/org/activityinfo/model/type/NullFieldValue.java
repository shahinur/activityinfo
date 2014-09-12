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

import org.activityinfo.model.form.FormClassVisitor;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Record;

/**
 * @author yuriyz on 8/14/14.
 */
public class NullFieldValue implements FieldValue {

    public static final NullFieldValue INSTANCE = new NullFieldValue();

    @Override
    public FieldTypeClass getTypeClass() {
        return new FieldTypeClass() {
            @Override
            public String getId() {
                return "null";
            }

            @Override
            public String getLabel() {
                return "null";
            }

            @Override
            public FieldType createType() {
                return new FieldType() {
                    @Override
                    public FieldTypeClass getTypeClass() {
                        return null;
                    }

                    @Override
                    public <T> T accept(FormField field, FormClassVisitor<T> visitor) {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public Record asRecord() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }


}
