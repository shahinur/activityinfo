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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.activityinfo.legacy.shared.command.CloneDatabase;
import org.activityinfo.legacy.shared.command.GetFormClass;
import org.activityinfo.legacy.shared.command.UpdateFormClass;
import org.activityinfo.legacy.shared.command.result.CreateResult;
import org.activityinfo.legacy.shared.command.result.FormClassResult;
import org.activityinfo.legacy.shared.command.result.VoidResult;
import org.activityinfo.legacy.shared.impl.CommandHandlerAsync;
import org.activityinfo.legacy.shared.impl.ExecutionContext;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.promise.Promise;
import org.activityinfo.server.command.handler.crud.ActivityPolicy;
import org.activityinfo.server.command.handler.crud.PropertyMap;
import org.activityinfo.server.command.handler.crud.UserDatabasePolicy;
import org.activityinfo.server.database.hibernate.entity.*;
import org.activityinfo.server.endpoint.gwtrpc.RemoteExecutionContext;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author yuriyz on 11/17/2014.
 */
public class CloneDatabaseHandler implements CommandHandlerAsync<CloneDatabase, CreateResult> {

    private static final Logger LOGGER = Logger.getLogger(CloneDatabaseHandler.class.getName());

    private final Injector injector;
    private final UserDatabasePolicy policy;
    private final EntityManager em;

    @Inject
    public CloneDatabaseHandler(Injector injector) {
        this.injector = injector;
        this.policy = injector.getInstance(UserDatabasePolicy.class);
        this.em = injector.getInstance(EntityManager.class);
    }

    @Override
    public void execute(CloneDatabase command, ExecutionContext context, final AsyncCallback<CreateResult> callback) {
        final User user = ((RemoteExecutionContext) context).retrieveUserEntity();
        final Integer newDbId = createDatabase(command, user);

        UserDatabase sourceDb = policy.findById(command.getSourceDatabaseId());
        UserDatabase targetDb = policy.findById(newDbId);

        // if the new countryId of the target database is different than the countryId of sourceDatabase,
        // copyData must be false -> skip copy
        if (sourceDb.getCountry().getId() != targetDb.getCountry().getId()) {
            callback.onSuccess(new CreateResult(newDbId));
            return;
        }

        List<Promise<Void>> promises = new ArrayList<>();

        if (command.isCopyData()) {
            promises.add(copyFormData(sourceDb, targetDb, user, context));
        }

        if (command.isCopyPartners() || command.isCopyUsers()) {
            copyPartners(sourceDb, targetDb, user, context);
        }

        if (command.isCopyUsers()) {
            copyUserPermissions(sourceDb, targetDb, user, context);
        }

        Promise.waitAll(promises).then(new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Void result) {
                callback.onSuccess(new CreateResult(newDbId));
            }
        });
    }

    private void copyUserPermissions(UserDatabase sourceDb, UserDatabase targetDb, User user, ExecutionContext context) {
        for (UserPermission sourcePermission : sourceDb.getUserPermissions()) {
            UserPermission newPermission = new UserPermission(sourcePermission);
            newPermission.setDatabase(targetDb);
            newPermission.setUser(user);
            newPermission.setLastSchemaUpdate(new Date());
            em.persist(newPermission);
        }
    }

    private void copyPartners(UserDatabase sourceDb, UserDatabase targetDb, User user, ExecutionContext context) {
        for (Partner partner : sourceDb.getPartners()) {
            Partner newPartner = new Partner();
            newPartner.setName(partner.getName());
            newPartner.setFullName(partner.getFullName());

            em.persist(newPartner);
            targetDb.getPartners().add(newPartner);
        }

        targetDb.setLastSchemaUpdate(new Date());
        em.persist(targetDb);
    }

    private Promise<Void> copyFormData(UserDatabase sourceDb, UserDatabase targetDb, User user, final ExecutionContext context) {
        List<Promise<VoidResult>> updatePromises = new ArrayList<>();
        for (Activity activity : sourceDb.getActivities()) {
            final Activity newActivity = copyActivity(activity, targetDb);

            final ResourceId sourceFormClass = CuidAdapter.activityFormClass(activity.getId());
            final ResourceId targetFormClass = CuidAdapter.activityFormClass(newActivity.getId());

            final Promise<VoidResult> updatePromise = new Promise<>();
            updatePromises.add(updatePromise);

            // form class
            context.execute(new GetFormClass(sourceFormClass), new AsyncCallback<FormClassResult>() {
                @Override
                public void onFailure(Throwable caught) {
                    LOGGER.log(Level.SEVERE, caught.getMessage(), caught);
                    updatePromise.onFailure(caught);
                }

                @Override
                public void onSuccess(FormClassResult result) {
                    FormClass formClass = result.getFormClass();
                    formClass.setId(targetFormClass);
                    context.execute(new UpdateFormClass(formClass), updatePromise);
                }
            });

            // form instance
            // todo ?
        }
        return Promise.waitAll(updatePromises);
    }

    private Activity copyActivity(Activity sourceActivity, UserDatabase targetDb) {
        Activity newActivity = new Activity(sourceActivity);
        newActivity.setDatabase(targetDb);
        ActivityPolicy policy = injector.getInstance(ActivityPolicy.class);
        policy.persist(newActivity);
        return newActivity;
    }

    private int createDatabase(CloneDatabase command, User user) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("name", command.getName());
        properties.put("countryId", command.getCountryId());

        return (Integer) policy.create(user, new PropertyMap(properties));
    }
}
