package org.activityinfo.server.command.handler.sync;

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

import com.bedatadriven.rebar.sql.client.query.SqlQuery;
import com.bedatadriven.rebar.sync.server.JpaUpdateBuilder;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import org.activityinfo.legacy.shared.command.GetSyncRegionUpdates;
import org.activityinfo.legacy.shared.command.result.SyncRegionUpdate;
import org.activityinfo.legacy.shared.impl.Tables;
import org.activityinfo.server.database.hibernate.dao.HibernateDAOProvider;
import org.activityinfo.server.database.hibernate.dao.UserDatabaseDAO;
import org.activityinfo.server.database.hibernate.entity.*;
import org.json.JSONException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static com.bedatadriven.rebar.sql.client.query.SqlQuery.*;

public class SchemaUpdateBuilder implements UpdateBuilder {

    private static final Logger LOGGER = Logger.getLogger(SchemaUpdateBuilder.class.getName());

    private final UserDatabaseDAO userDatabaseDAO;
    private final EntityManager entityManager;

    private SqliteBatchBuilder batch;
    private Set<Integer> databaseIds = Sets.newHashSet();

    private List<UserDatabase> databases = new ArrayList<>();
    private List<UserPermission> userPermissions;
    private User user;


    @Inject
    public SchemaUpdateBuilder(EntityManagerFactory entityManagerFactory) {
        // create a new, unfiltered entity manager so we can see deleted records
        this.entityManager = entityManagerFactory.createEntityManager();
        this.userDatabaseDAO = HibernateDAOProvider.makeImplementation(UserDatabaseDAO.class,
                UserDatabase.class,
                entityManager);
    }

    @SuppressWarnings("unchecked") @Override
    public SyncRegionUpdate build(User user, GetSyncRegionUpdates request) throws JSONException, IOException {

        batch = new SqliteBatchBuilder();
        this.user = user;

        try {
            // get the permissions before we apply the filter
            // otherwise they will be excluded
            userPermissions = entityManager.createQuery("select p from UserPermission p where p.user.id = ?1")
                                           .setParameter(1, user.getId())
                                           .getResultList();

            DomainFilters.applyUserFilter(user, entityManager);

            databases = userDatabaseDAO.queryAllUserDatabasesAlphabetically();
            for(UserDatabase db : databases) {
                databaseIds.add(db.getId());
            }

            long localVersion = request.getLocalVersion() == null ? 0 : Long.parseLong(request.getLocalVersion());
            long serverVersion = getCurrentSchemaVersion();

            LOGGER.info("Schema versions: local = " + localVersion + ", server = " + serverVersion);

            SyncRegionUpdate update = new SyncRegionUpdate();
            update.setVersion(Long.toString(serverVersion));
            update.setComplete(true);

            if (localVersion < serverVersion) {
                update.setSql(buildSql());
            }
            return update;
        } finally {
            entityManager.close();
        }
    }

    private String buildSql() throws JSONException, IOException {


        batch.createTablesIfNotExist(
                entityManager,
                Tables.COUNTRY,
                Tables.ADMIN_LEVEL,
                Tables.LOCATION_TYPE,
                Tables.USER_DATABASE,
                Tables.USER_LOGIN,
                Tables.USER_PERMISSION,
                Tables.ACTIVITY,
                Tables.INDICATOR,
                Tables.INDICATOR_LINK,
                Tables.ATTRIBUTE,
                Tables.ATTRIBUTE_GROUP,
                Tables.ATTRIBUTE_GROUP_IN_ACTIVITY,
                Tables.PARTNER_IN_DATABASE,
                Tables.PARTNER,
                Tables.TARGET,
                Tables.PROJECT,
                Tables.LOCKED_PERIOD);


        SqlQuery countryIds = select("countryId")
                .from(Tables.USER_DATABASE)
                .where("databaseId").in(databaseIds);

        SqlQuery activityIds = select("activityId")
                .from(Tables.ACTIVITY)
                .where("databaseId").in(databaseIds);


        batch.clearTables(
                Tables.COUNTRY,
                Tables.ADMIN_LEVEL,
                Tables.LOCATION_TYPE,
                Tables.USER_DATABASE,
                Tables.USER_PERMISSION,
                Tables.ACTIVITY,
                Tables.PARTNER_IN_DATABASE,
                Tables.LOCKED_PERIOD);

        batch.insert().from(selectAll().from(Tables.COUNTRY).where("countryId").in(countryIds))
             .into(Tables.COUNTRY)
             .execute(entityManager);
        batch.insert().from(selectAll().from(Tables.ADMIN_LEVEL).where("countryId").in(countryIds))
             .into(Tables.ADMIN_LEVEL)
             .execute(entityManager);
        batch.insert().from(selectAll().from(Tables.LOCATION_TYPE).where("countryId").in(countryIds))
             .into(Tables.LOCATION_TYPE)
             .execute(entityManager);
        batch.insert().from(selectAll().from(Tables.USER_DATABASE).where("countryId").in(countryIds))
             .into(Tables.USER_DATABASE)
             .execute(entityManager);
        batch.insert().from(selectAll().from(Tables.USER_PERMISSION)
               .where("databaseId")
               .in(databaseIds)
               .where("userId")
               .equalTo(user.getId()))
                .into(Tables.USER_PERMISSION)
               .execute(entityManager);

        batch.insert().from(selectAll().from(Tables.ACTIVITY).where("databaseId").in(databaseIds))
             .into(Tables.ACTIVITY)
             .execute(entityManager);
        batch.insert().from(selectAll().from(Tables.PARTNER_IN_DATABASE).where("databaseId").in(databaseIds))
             .into(Tables.PARTNER_IN_DATABASE)
             .execute(entityManager);
        batch.insert().from(selectAll().from(Tables.PARTNER).where("partnerId").in(
                select("partnerId").from(Tables.PARTNER_IN_DATABASE).where("databaseId").in(databaseIds)))
                .into(Tables.PARTNER)
                .execute(entityManager);
        batch.insert().from(selectAll().from(Tables.PROJECT).where("databaseId").in(databaseIds))
                .into(Tables.PROJECT)
                .execute(entityManager);

        batch.insert().from(selectAll().from(Tables.LOCKED_PERIOD).where("UserDatabaseId").in(databaseIds))
             .into(Tables.LOCKED_PERIOD)
             .execute(entityManager);
        batch.insert().from(selectAll().from(Tables.LOCKED_PERIOD).where("activityId").in(activityIds))
             .into(Tables.LOCKED_PERIOD)
             .execute(entityManager);
        batch.insert().from(selectAll().from(Tables.LOCKED_PERIOD).where("projectId").in(
                select("projectId").from(Tables.PROJECT).where("databaseId").in(databaseIds)))
             .into(Tables.LOCKED_PERIOD)
             .execute(entityManager);

        batch.insert().from(select("UserId", "Name", "Email")
            .from(Tables.USER_LOGIN)
            .where("userId").in(
                select("OwnerUserId")
                    .from(Tables.USER_DATABASE).where("databaseId").in(databaseIds)))
            .into(Tables.USER_LOGIN)
            .execute(entityManager);

        batch.insert().from(selectAll().from(Tables.INDICATOR)
            .where("activityId").in(activityIds))
            .into(Tables.INDICATOR)
            .execute(entityManager);

        batch.insert().from(selectAll().from(Tables.ATTRIBUTE_GROUP_IN_ACTIVITY)
            .where("activityId").in(activityIds))
            .into(Tables.ATTRIBUTE_GROUP_IN_ACTIVITY)
            .execute(entityManager);

        SqlQuery groupIds = select("attributeGroupId")
            .from(Tables.ATTRIBUTE_GROUP_IN_ACTIVITY)
            .where("activityId").in(activityIds);

        batch.insert().from(selectAll().from(Tables.ATTRIBUTE_GROUP)
            .where("attributeGroupId").in(groupIds))
            .into(Tables.ATTRIBUTE_GROUP)
            .execute(entityManager);

        batch.insert().from(selectAll().from(Tables.ATTRIBUTE)
            .where("attributeGroupId").in(groupIds))
            .into(Tables.ATTRIBUTE)
            .execute(entityManager);

        SqlQuery indicatorIds = select("indicatorId")
            .from(Tables.INDICATOR)
            .where("activityId").in(activityIds);

        batch.insert().from(selectAll().from(Tables.INDICATOR_LINK)
            .where("sourceIndicatorId").in(indicatorIds))
            .into(Tables.INDICATOR_LINK)
            .execute(entityManager);

        batch.insert().from(selectAll().from(Tables.INDICATOR_LINK)
           .where("destinationIndicatorId").in(indicatorIds))
           .into(Tables.INDICATOR_LINK)
           .execute(entityManager);

        return batch.build();
    }

    public long getCurrentSchemaVersion() {
        long currentVersion = 1;
        for (UserDatabase db : databases) {
            if (db.getVersion() > currentVersion) {
                currentVersion = db.getVersion();
            }
        }
        for (UserPermission perm : userPermissions) {
            if (perm.getVersion() > currentVersion) {
                currentVersion = perm.getVersion();
            }
        }
        return currentVersion;
    }
}
