package org.activityinfo.server.command.handler.crud;

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

import com.google.inject.Inject;
import org.activityinfo.legacy.shared.model.LocationTypeDTO;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.server.command.handler.PermissionOracle;
import org.activityinfo.server.database.hibernate.dao.ActivityDAO;
import org.activityinfo.server.database.hibernate.dao.UserDatabaseDAO;
import org.activityinfo.server.database.hibernate.entity.Activity;
import org.activityinfo.server.database.hibernate.entity.LocationType;
import org.activityinfo.server.database.hibernate.entity.User;
import org.activityinfo.server.database.hibernate.entity.UserDatabase;
import org.activityinfo.service.store.CommitStatus;
import org.activityinfo.service.store.ResourceStore;
import org.activityinfo.service.store.UpdateResult;

import javax.persistence.EntityManager;
import java.util.Date;

public class ActivityPolicy implements EntityPolicy<Activity> {

    private final EntityManager em;
    private final ActivityDAO activityDAO;
    private final UserDatabaseDAO databaseDAO;
    private final ResourceStore resourceStore;

    @Inject
    public ActivityPolicy(EntityManager em,
                          ActivityDAO activityDAO,
                          UserDatabaseDAO databaseDAO,
                          ResourceStore resourceStore) {
        this.em = em;
        this.activityDAO = activityDAO;
        this.databaseDAO = databaseDAO;
        this.resourceStore = resourceStore;
    }

    @Override
    public Object create(User user, PropertyMap properties) {

        UserDatabase database = getDatabase(properties);
        PermissionOracle.using(em).assertDesignPrivileges(database, user);

        // create the entity
        Activity activity = new Activity();
        activity.setDatabase(database);
        activity.setSortOrder(calculateNextSortOrderIndex(database.getId()));

        // NO LONGER USED: but needed for db
        activity.setLocationType(em.find(LocationType.class, 20301));

        applyProperties(activity, properties);

        activityDAO.persist(activity);

        FormClass formClass = new FormClass(CuidAdapter.activityFormClass(activity.getId()));
        formClass.setLabel(activity.getName());
        formClass.setOwnerId(CuidAdapter.databaseId(activity.getDatabase().getId()));

        UpdateResult result = resourceStore.put(user.asAuthenticatedUser(), formClass.asResource());
        if(result.getStatus() != CommitStatus.COMMITTED) {
            throw new RuntimeException("Failed to commit");
        }

        return activity.getId();
    }

    @Override
    public void update(User user, Object entityId, PropertyMap changes) {
        Activity activity = em.find(Activity.class, entityId);

        PermissionOracle.using(em).assertDesignPrivileges(activity.getDatabase(), user);

        applyProperties(activity, changes);
    }

    private UserDatabase getDatabase(PropertyMap properties) {
        int databaseId = (Integer) properties.get("databaseId");

        return databaseDAO.findById(databaseId);
    }

    private LocationType getLocationType(PropertyMap properties) {
        int locationTypeId = ((Integer) properties.get("locationTypeId"));
        return em.getReference(LocationType.class, locationTypeId);
    }

    private Integer calculateNextSortOrderIndex(int databaseId) {
        Integer maxSortOrder = activityDAO.queryMaxSortOrder(databaseId);
        return maxSortOrder == null ? 1 : maxSortOrder + 1;
    }

    private void applyProperties(Activity activity, PropertyMap changes) {
        if (changes.containsKey("name")) {
            activity.setName((String) changes.get("name"));
        }


        if (changes.containsKey("locationType")) {
            activity.setLocationType(em.getReference(LocationType.class,
                    ((LocationTypeDTO) changes.get("locationType")).getId()));
        }

        if (changes.containsKey("published")) {
            activity.setPublished((Integer) changes.get("published"));
        }

        if (changes.containsKey("sortOrder")) {
            activity.setSortOrder((Integer) changes.get("sortOrder"));
        }

        activity.getDatabase().setLastSchemaUpdate(new Date());
    }
}
