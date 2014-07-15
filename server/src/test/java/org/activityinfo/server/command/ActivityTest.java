package org.activityinfo.server.command;

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

import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.legacy.shared.adapter.ResourceLocatorAdaptor;
import org.activityinfo.legacy.shared.command.BatchCommand;
import org.activityinfo.legacy.shared.command.CreateEntity;
import org.activityinfo.legacy.shared.command.GetSchema;
import org.activityinfo.legacy.shared.command.UpdateEntity;
import org.activityinfo.legacy.shared.command.result.CreateResult;
import org.activityinfo.legacy.shared.exception.CommandException;
import org.activityinfo.legacy.shared.model.*;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.server.database.OnDataSet;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static org.activityinfo.core.client.PromiseMatchers.assertResolves;
import static org.activityinfo.legacy.shared.adapter.CuidAdapter.activityFormClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/schema1.db.xml")
public class ActivityTest extends CommandTestCase2 {

    @Before
    public void setUser() {
        setUser(1);
    }

    @Test
    public void testActivity() throws CommandException {

        /*
         * Initial data load
         */

        SchemaDTO schema = execute(new GetSchema());

        UserDatabaseDTO db = schema.getDatabaseById(1);

        /*
         * Create a new activity
         */

        LocationTypeDTO locType = schema.getCountryById(1).getLocationTypes().get(0);

        ActivityDTO act = new ActivityDTO();
        act.setName("Warshing the dishes");
        act.setLocationType(locType);
        act.setReportingFrequency(ActivityDTO.REPORT_MONTHLY);

        CreateResult cresult = execute(CreateEntity.Activity(db, act));

        int newId = cresult.getNewId();

        /*
         * Reload schema to verify the changes have stuck
         */

        schema = execute(new GetSchema());

        act = schema.getActivityById(newId);

        assertEquals("name", "Warshing the dishes", act.getName());
        assertEquals("locationType", locType.getName(), act.getLocationType().getName());
        assertEquals("reportingFrequency", ActivityDTO.REPORT_MONTHLY, act.getReportingFrequency());
        assertEquals("public", Published.NOT_PUBLISHED.getIndex(), act.getPublished());

    }

    @Test
    public void updateSortOrderTest() throws Throwable {

        /* Update Sort Order */
        Map<String, Object> changes1 = new HashMap<String, Object>();
        changes1.put("sortOrder", 2);
        Map<String, Object> changes2 = new HashMap<String, Object>();
        changes2.put("sortOrder", 1);

        execute(new BatchCommand(
                new UpdateEntity("Activity", 1, changes1),
                new UpdateEntity("Activity", 2, changes2)));

        /* Confirm the order is changed */

        SchemaDTO schema = execute(new GetSchema());
        assertEquals(2, schema.getDatabaseById(1).getActivities().get(0).getId());
        assertEquals(1, schema.getDatabaseById(1).getActivities().get(1).getId());
    }

    @Test
    public void updatePublished() throws Throwable {

        /* Update Sort Order */
        Map<String, Object> changes = new HashMap<String, Object>();
        changes.put("published", Published.ALL_ARE_PUBLISHED.getIndex());

        execute(new UpdateEntity("Activity", 1, changes));

        /* Confirm the order is changed */

        SchemaDTO schema = execute(new GetSchema());
        assertEquals(Published.ALL_ARE_PUBLISHED.getIndex(), schema.getActivityById(1).getPublished());
    }

    @Test
    public void testFormPersister() {

        SchemaDTO schema = execute(new GetSchema());
        UserDatabaseDTO db = schema.getDatabaseById(1);
        LocationTypeDTO locType = schema.getCountryById(1).getLocationTypes().get(0);


        ActivityDTO act = new ActivityDTO();
        act.setName("Household Survey");
        act.setLocationType(locType);
        act.setReportingFrequency(ActivityDTO.REPORT_ONCE);


        CreateResult createResult = execute(CreateEntity.Activity(db, act));
        ResourceId classId = activityFormClass(createResult.getNewId());

        ResourceLocatorAdaptor resourceLocator = new ResourceLocatorAdaptor(getDispatcher());
        FormClass formClass = assertResolves(resourceLocator.getFormClass(classId));

        FormField newField = new FormField(ResourceId.generateId());
        newField.setLabel("How old are you?");
        newField.setType(new QuantityType().setUnits("years"));
        formClass.addElement(newField);

        assertResolves(resourceLocator.persist(formClass));

        newField.setLabel("How old are you today?");
        // save again
        assertResolves(resourceLocator.persist(formClass));

        FormClass reform = assertResolves(resourceLocator.getFormClass(formClass.getId()));
        System.out.println(reform.getFields().toString());

        assertThat(reform.getFields(), Matchers.hasSize(7));


    }
}
