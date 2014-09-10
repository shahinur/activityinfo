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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import org.activityinfo.legacy.shared.command.*;
import org.activityinfo.legacy.shared.command.result.MonthlyReportResult;
import org.activityinfo.legacy.shared.command.result.SiteResult;
import org.activityinfo.legacy.shared.model.AttributeDTO;
import org.activityinfo.legacy.shared.model.IndicatorRowDTO;
import org.activityinfo.legacy.shared.model.SiteDTO;
import org.activityinfo.legacy.shared.util.Collector;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.fixtures.MockHibernateModule;
import org.activityinfo.fixtures.Modules;
import org.activityinfo.server.command.handler.sync.TimestampHelper;
import org.activityinfo.server.database.OnDataSet;
import org.activityinfo.server.database.hibernate.entity.AdminEntity;
import org.activityinfo.server.database.hibernate.entity.Location;
import org.activityinfo.server.database.hibernate.entity.LocationType;
import org.activityinfo.server.endpoint.gwtrpc.GwtRpcModule;
import org.activityinfo.server.util.logging.LoggingModule;
import org.activityinfo.legacy.client.KeyGenerator;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.persistence.EntityManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.activityinfo.legacy.shared.command.UpdateMonthlyReports.Change;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.*;

@RunWith(InjectionSupport.class)
@Modules({
        MockHibernateModule.class,
        GwtRpcModule.class,
        LoggingModule.class
})
public class SyncIntegrationTest extends LocalHandlerTestCase {
    @Inject
    private KeyGenerator keyGenerator;

    @Before
    public void setupLogging() {
        Logger.getLogger("org.hibernate").setLevel(Level.ALL);
    }

    private long nowIsh;

    @Test
    @OnDataSet("/dbunit/sites-simple1.db.xml")
    public void run() throws SQLException, InterruptedException {
        synchronizeFirstTime();

        Collector<Date> lastUpdate = Collector.newCollector();
        syncHistoryTable.get(lastUpdate);

        assertThat(lastUpdate.getResult(), is(not(nullValue())));

        assertThat(
                queryString("select Name from Indicator where IndicatorId=103"),
                equalTo("Nb. of distributions"));
        assertThat(
                queryString("select Name from AdminLevel where AdminLevelId=1"),
                equalTo("Province"));
        assertThat(
                queryString("select Name from AdminEntity where AdminEntityId=21"),
                equalTo("Irumu"));
        assertThat(queryString("select Name from Location where LocationId=7"),
                equalTo("Shabunda"));
        assertThat(
                queryInt("select value from IndicatorValue where ReportingPeriodId=601 and IndicatorId=6"),
                equalTo(35));

        assertThat(
                queryInt("select PartnerId from partnerInDatabase where databaseid=2"),
                equalTo(1));

        assertThat(
                queryInt("select AttributeGroupId  from AttributeGroupInActivity where ActivityId=2"),
                equalTo(1));

        assertThat(queryInt("select count(*) from LockedPeriod"), equalTo(4));
        assertThat(
                queryInt("select count(*) from LockedPeriod where ProjectId is not null"),
                equalTo(1));
        assertThat(
                queryInt("select count(*) from LockedPeriod where ActivityId is not null"),
                equalTo(1));
        assertThat(
                queryInt("select count(*) from LockedPeriod where UserDatabaseId is not null"),
                equalTo(2));

        // / now try updating a site remotely (from another client)
        // and veryify that we get the update after we synchronized

        Thread.sleep(1000);

        SiteDTO s1 = executeLocally(GetSites.byId(1)).getData().get(0);
        assertThat((Double) s1.getIndicatorValue(1), equalTo(1500d));

        Map<String, Object> changes = Maps.newHashMap();
        changes.put(AttributeDTO.getPropertyName(1), true);
        changes.put("comments", "newComments");
        executeRemotely(new UpdateSite(1, changes));

        synchronize();

        s1 = executeLocally(GetSites.byId(1)).getData().get(0);

        assertThat(s1.getAttributeValue(1), equalTo(true));
        assertThat(s1.getAttributeValue(2), equalTo(false));
        assertThat(s1.getComments(), equalTo("newComments"));

        // old values are preserved...
        assertThat(s1.getIndicatorDoubleValue(1), equalTo(1500d));

        // Try deleting a site

        executeRemotely(new Delete("Site", 1));

        synchronize();

        SiteResult siteResult = executeLocally(GetSites.byId(1));

        assertThat(siteResult.getData().size(), equalTo(0));

        // Verify that we haven't toasted the other data

        SiteDTO site = executeLocally(GetSites.byId(3)).getData().get(0);
        assertThat(site.getIndicatorDoubleValue(1), equalTo(10000d));

    }

    @Test
    @OnDataSet("/dbunit/locations.db.xml")
    public void locationsAreChunked() throws SQLException, InterruptedException {
        addLocationsToServerDatabase(220);
        synchronizeFirstTime();

        assertThat(
                Integer.valueOf(queryString("select count(*) from Location")),
                equalTo(220));

        // update a location on the server
        serverEm.getTransaction().begin();
        Location location = (Location) serverEm.createQuery(
                "select l from Location l where l.name = 'Penekusu 26'")
                .getSingleResult();
        location.setAxe("Motown");
        location.setTimeEdited(new Date().getTime());
        serverEm.getTransaction().commit();

        newRequest();
        synchronize();

        // todo: store milliseconds in mysql rather than as
        // date time which has resolution limited to 1 second
        Thread.sleep(1000);

        assertThat(
                queryInt("select count(*) from Location where Name='Penekusu 26'"),
                equalTo(1));
        assertThat(
                queryString("select axe from Location where Name='Penekusu 26'"),
                equalTo("Motown"));

        // now create a new location
        Location newLocation = new Location();
        int locationId = keyGenerator.generateInt();
        newLocation.setName("Bukavu");
        newLocation.setTimeEdited(new Date().getTime());
        newLocation.setId(123456789);
        newLocation.setLocationType(serverEm.find(LocationType.class, 1));
        newLocation.setId(locationId);
        serverEm.getTransaction().begin();
        serverEm.persist(newLocation);
        serverEm.getTransaction().commit();

        newRequest();

        synchronize();

        assertThat(queryString("select name from Location where LocationId = "
                + locationId),
                equalTo("Bukavu"));
    }

    @Test
    @OnDataSet("/dbunit/sites-simple1.db.xml")
    public void testGetAdminEntities() throws SQLException, InterruptedException {
        synchronizeFirstTime();
        executeLocally(new GetAdminEntities(1));
    }

    @Test
    @OnDataSet("/dbunit/monthly-calc-indicators.db.xml")
    public void updateMonthlyReports() throws SQLException, InterruptedException {
        synchronizeFirstTime();

        int siteId = 1;

        MonthlyReportResult result = executeLocally(new GetMonthlyReports(siteId, new Month(2009, 1), 5));


        IndicatorRowDTO women = result.getData().get(0);
        IndicatorRowDTO men = result.getData().get(1);

        assertThat(women.getIndicatorName(), equalTo("women"));
        assertThat(women.getIndicatorId(), equalTo(7002));

        assertThat(men.getIndicatorName(), equalTo("men"));
        assertThat(men.getActivityName(), equalTo("NFI"));
        assertThat(men.getActivityId(), equalTo(901));
        assertThat(men.getIndicatorId(), equalTo(7001));

        assertThat(men.getValue(2009, 1), CoreMatchers.equalTo(200d));
        assertThat(women.getValue(2009, 1), CoreMatchers.equalTo(300d));

        assertThat(men.getValue(2009, 2), CoreMatchers.equalTo(150d));
        assertThat(women.getValue(2009, 2), CoreMatchers.equalTo(330d));

        // Update locally

        executeLocally(new UpdateMonthlyReports(siteId, Lists.newArrayList(
            new Change(men.getIndicatorId(), new Month(2009, 1), 221d),
            new Change(men.getIndicatorId(), new Month(2009, 3), 444d),
            new Change(women.getIndicatorId(), new Month(2009, 5), 200d),
            new Change(men.getIndicatorId(), new Month(2009, 5), 522d))));

        result = executeLocally(new GetMonthlyReports(siteId, new Month(2009, 1), 12));

        women = result.getData().get(0);
        men = result.getData().get(1);

        assertThat(men.getValue(2009, 1), CoreMatchers.equalTo(221d));
        assertThat(women.getValue(2009, 1), CoreMatchers.equalTo(300d));

        // same - no change
        assertThat(men.getValue(2009, 2), equalTo(150d));
        assertThat(women.getValue(2009, 2), equalTo(330d));

        // new periods
        assertThat(men.getValue(2009, 3), equalTo(444d));
        assertThat(women.getValue(2009, 5), equalTo(200d));
        assertThat(men.getValue(2009, 5), equalTo(522d));

        // Synchronize
        synchronize();

        newRequest();

        MonthlyReportResult remoteResult = executeRemotely(new GetMonthlyReports(siteId, new Month(2009, 1), 12));
        women = remoteResult.getData().get(0);
        men = remoteResult.getData().get(1);

        assertThat(men.getValue(2009, 1), CoreMatchers.equalTo(221d));
        assertThat(women.getValue(2009, 1), CoreMatchers.equalTo(300d));

        // same - no change
        assertThat(men.getValue(2009, 2), equalTo(150d));
        assertThat(women.getValue(2009, 2), equalTo(330d));

        // new periods
        assertThat(men.getValue(2009, 3), equalTo(444d));
        assertThat(women.getValue(2009, 5), equalTo(200d));
        assertThat(men.getValue(2009, 5), equalTo(522d));




    }

    @Test
    @OnDataSet("/dbunit/locations.db.xml")
    public void timeStampSurvivesRoundTrip() {
        EntityManager entityManager = serverEntityManagerFactory
                .createEntityManager();
        entityManager.getTransaction().begin();
        Location loc = new Location();
        loc.setTimeEdited(nowIsh += 1500);
        loc.setName("Penekusu");
        loc.setLocationType(entityManager.find(LocationType.class, 1));
        entityManager.persist(loc);
        entityManager.getTransaction().commit();
        entityManager.clear();

        entityManager.getTransaction().begin();
        Location loc2 = entityManager.find(Location.class, loc.getId());

        String tsString = TimestampHelper.toString(loc2.getTimeEdited());
        long ts = TimestampHelper.fromString(tsString);

        assertFalse(loc2 == loc);
        assertThat(loc2.getTimeEdited(), equalTo(ts));
        entityManager.getTransaction().commit();
        entityManager.clear();

        entityManager.getTransaction().begin();
        Location loc3 = entityManager.find(Location.class, loc.getId());

        assertFalse(ts > loc3.getTimeEdited());
        assertFalse(ts < loc3.getTimeEdited());

        entityManager.close();

    }

    private void addLocationsToServerDatabase(int count) {

        nowIsh = new Date().getTime();

        EntityManager entityManager = serverEntityManagerFactory
                .createEntityManager();
        entityManager.getTransaction().begin();
        for (int i = 1; i <= count; ++i) {
            Location loc = new Location();
            loc.setId(i);
            loc.setTimeEdited(nowIsh += 15000);
            loc.setName("Penekusu " + i);
            loc.getAdminEntities().add(
                    entityManager.getReference(AdminEntity.class, 2));
            loc.getAdminEntities().add(
                    entityManager.getReference(AdminEntity.class, 12));
            loc.setLocationType(entityManager.find(LocationType.class, 1));
            entityManager.persist(loc);
            entityManager.flush();

            assertTrue(loc.getId() != 0);
        }
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    private String queryString(String sql) throws SQLException {
        return localDatabase.selectString(sql);
    }

    private Integer queryInt(String sql) throws SQLException {
        return localDatabase.selectInt(sql);
    }

}
