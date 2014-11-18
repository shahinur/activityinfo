package org.activityinfo.server.command.handler;
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

import com.google.api.client.util.Sets;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.ResourceId;

import java.util.Set;

import static org.activityinfo.model.legacy.CuidAdapter.*;

/**
 * @author yuriyz on 11/17/2014.
 */
public class FormClassTrash {

    public static final int[] BUILTIN_FIELDS = new int[] {
            START_DATE_FIELD, END_DATE_FIELD, PARTNER_FIELD, PROJECT_FIELD,
            LOCATION_FIELD, COMMENT_FIELD };

    private FormClassTrash() {
    }

    /**
     * HACK : If formClass id is changed all built-in form fields ids must be changed as well:
     *
     * Old formClass id: a0000000063
     * FormField{id=a00000000630000000007, label=Partner, type=Reference}
     *
     * New formClassId : a0000000072
     * FormField id is changed: a00000000630000000007 -> a00000000720000000007
     *
     *
     * @param formClass form class with new id
     * @param oldId old form class id
     */
    public static void normalizeBuiltInFormClassFields(FormClass formClass, ResourceId oldId) {
        // reset ids of built-in fields

        Set<FormField> toRemove = Sets.newHashSet();
        Set<FormField> toAdd = Sets.newHashSet();

        for (FormField formField : formClass.getFields()) {
            for(int fieldIndex : BUILTIN_FIELDS) {
                if (formField.getId().equals(CuidAdapter.field(oldId, fieldIndex))) {
                    toRemove.add(formField);

                    // convert to record to change id -> we don't want to allow change id directly in FormField
                    Record record = formField.asRecord();
                    record.set("id", CuidAdapter.field(formClass.getId(), fieldIndex));
                    toAdd.add(FormField.fromRecord(record));
                }
            }
        }
        formClass.getElements().removeAll(toRemove);
        formClass.getElements().addAll(toAdd);
    }
}
