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

import com.bedatadriven.rebar.sql.server.jdbc.JdbcScheduler;
import com.bedatadriven.rebar.time.calendar.LocalDate;
import org.activityinfo.core.client.InstanceQuery;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.criteria.ClassCriteria;
import org.activityinfo.core.shared.criteria.CriteriaIntersection;
import org.activityinfo.core.shared.criteria.ParentCriteria;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.legacy.shared.command.GetSchema;
import org.activityinfo.legacy.shared.exception.CommandException;
import org.activityinfo.legacy.shared.model.ActivityDTO;
import org.activityinfo.legacy.shared.model.AdminLevelDTO;
import org.activityinfo.legacy.shared.model.LockedPeriodSet;
import org.activityinfo.legacy.shared.model.SchemaDTO;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.system.ApplicationProperties;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.promise.Promise;
import org.activityinfo.server.database.OnDataSet;
import org.activityinfo.ui.client.service.TestResourceLocator;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static org.activityinfo.core.client.PromiseMatchers.assertResolves;
import static org.activityinfo.core.client.PromiseMatchers.resolvesTo;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/sites-simple1.db.xml")
public class GetSchemaTest extends CommandTestCase2 {

    @Before
    public void cleanUpScheduler() {
        JdbcScheduler.get().forceCleanup();
    }

    @Test
    public void testDatabaseVisibilityForOwners() throws CommandException {

        // owners should be able to see their databases

        setUser(1); // Alex

        SchemaDTO schema = execute(new GetSchema());

        assertThat("database count", schema.getDatabases(), hasSize(3));
        assertThat("database list is sorted", schema.getDatabases().get(0).getName(), equalTo("Alpha"));

        assertTrue("ALEX(owner) in PEAR", schema.getDatabaseById(1) != null); // PEAR
        assertTrue("ALEX can design", schema.getDatabaseById(1).isDesignAllowed());
        assertTrue("Alex can edit all", schema.getDatabaseById(1).isEditAllowed());
        assertTrue("object graph is preserved",
                schema.getDatabaseById(1).getCountry() ==
                schema.getDatabaseById(2).getCountry());


        ActivityDTO nfi = schema.getDatabaseById(1).getActivities().get(0);

        assertTrue("object graph is preserved (database-activity)",
                schema.getDatabaseById(1) == nfi.getDatabase());
        assertThat(nfi.getLocationTypeId(), equalTo(1));
        assertThat(nfi.<Integer>get("locationTypeId"), equalTo(1));

        AdminLevelDTO adminLevel = schema.getCountries().get(0).getAdminLevels().get(0);
        assertThat("CountryId is not null", adminLevel.getCountryId(), not(equalTo(0)));
        assertThat("CountryId is not null", adminLevel.getId(), not(equalTo(0)));

        assertTrue("CountryId is not null",
                schema.getCountries().get(0).getAdminLevels().get(0).getCountryId() != 0);

    }

    @Test
    @OnDataSet("/dbunit/sites-public.db.xml")
    public void testDatabasePublished() throws CommandException {

        // Anonymouse user should fetch schema database with published
        // activities.
        setUser(0);

        SchemaDTO schema = execute(new GetSchema());

        assertThat(schema.getDatabases().size(), equalTo(1));
    }

    @Test
    public void testLockedProjects() {
        setUser(1);
        SchemaDTO schema = execute(new GetSchema());

        assertThat(schema.getProjectById(1).getLockedPeriods().size(),
                equalTo(1));

        LockedPeriodSet locks = new LockedPeriodSet(schema);
        assertTrue(locks.isProjectLocked(1, new LocalDate(2009, 1, 1)));
        assertTrue(locks.isProjectLocked(1, new LocalDate(2009, 1, 6)));
        assertTrue(locks.isProjectLocked(1, new LocalDate(2009, 1, 12)));
        assertFalse(locks.isProjectLocked(1, new LocalDate(2008, 1, 12)));
        assertFalse(locks.isProjectLocked(1, new LocalDate(2010, 1, 12)));

    }

    @Test
    public void testDatabaseVisibilityForView() throws CommandException {

        setUser(2); // Bavon

        SchemaDTO schema = execute(new GetSchema());

        assertThat(schema.getDatabases().size(), equalTo(2));
        assertThat("BAVON in PEAR", schema.getDatabaseById(1), is(not(nullValue())));
        assertThat(schema.getDatabaseById(1).getMyPartnerId(), equalTo(1));
        assertThat(schema.getDatabaseById(1).isEditAllowed(), equalTo(true));
        assertThat(schema.getDatabaseById(1).isEditAllAllowed(), equalTo(false));
    }

    @Test
    public void testDatabaseVisibilityNone() throws CommandException {
        setUser(3); // Stefan

        SchemaDTO schema = execute(new GetSchema());

        assertTrue("STEFAN does not have access to RRM", schema.getDatabaseById(2) == null);
    }


    @Test
    public void newApiTest() throws IOException {

        ResourceLocator locator = new TestResourceLocator("/dbunit/sites-simple1.json");

        Promise<FormClass> userForm = locator.getFormClass(CuidAdapter.activityFormClass(1));

        assertThat(userForm, resolvesTo(CoreMatchers.<FormClass>notNullValue()));
    }


}
