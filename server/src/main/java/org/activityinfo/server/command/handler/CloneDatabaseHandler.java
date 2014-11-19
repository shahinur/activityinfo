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

import com.google.common.base.Function;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.activityinfo.legacy.client.remote.AbstractDispatcher;
import org.activityinfo.legacy.shared.adapter.SitePersister;
import org.activityinfo.legacy.shared.adapter.bindings.SiteBinding;
import org.activityinfo.legacy.shared.command.*;
import org.activityinfo.legacy.shared.command.result.*;
import org.activityinfo.legacy.shared.impl.CommandHandlerAsync;
import org.activityinfo.legacy.shared.impl.ExecutionContext;
import org.activityinfo.legacy.shared.model.ActivityFormDTO;
import org.activityinfo.legacy.shared.model.SiteDTO;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.promise.Promise;
import org.activityinfo.server.command.handler.crud.ActivityPolicy;
import org.activityinfo.server.database.hibernate.entity.*;
import org.activityinfo.server.endpoint.gwtrpc.RemoteExecutionContext;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author yuriyz on 11/17/2014.
 */
public class CloneDatabaseHandler implements CommandHandlerAsync<CloneDatabase, CreateResult> {

    private static final Logger LOGGER = Logger.getLogger(CloneDatabaseHandler.class.getName());

    private final Injector injector;
    private final EntityManager em;

    @Inject
    public CloneDatabaseHandler(Injector injector) {
        this.injector = injector;
        this.em = injector.getInstance(EntityManager.class);
    }

    @Override
    public void execute(CloneDatabase command, ExecutionContext context, final AsyncCallback<CreateResult> callback) {
        final User user = ((RemoteExecutionContext) context).retrieveUserEntity();
        final UserDatabase targetDb = createDatabase(command, user);

        UserDatabase sourceDb = em.find(UserDatabase.class, command.getSourceDatabaseId());

        // if the new countryId of the target database is different than the countryId of sourceDatabase,
        // copyData must be false -> skip copy
        if (sourceDb.getCountry().getId() != targetDb.getCountry().getId()) {
            callback.onSuccess(new CreateResult(targetDb.getId()));
            return;
        }

        List<Promise<Void>> promises = new ArrayList<>();

        if (command.isCopyData()) {
            promises.add(copyFormData(sourceDb, targetDb, context));
        }

        if (command.isCopyPartners() || command.isCopyUsers()) {
            copyPartners(sourceDb, targetDb);
        }

        if (command.isCopyUsers()) {
            copyUserPermissions(sourceDb, targetDb, user);
        }

        Promise.waitAll(promises).then(new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Void result) {
                callback.onSuccess(new CreateResult(targetDb.getId()));
            }
        });
    }

    private void copyUserPermissions(UserDatabase sourceDb, UserDatabase targetDb, User user) {
        for (UserPermission sourcePermission : sourceDb.getUserPermissions()) {
            UserPermission newPermission = new UserPermission(sourcePermission);
            newPermission.setDatabase(targetDb);
            newPermission.setUser(user);
            newPermission.setLastSchemaUpdate(new Date());
            em.persist(newPermission);
        }
    }

    private void copyPartners(UserDatabase sourceDb, UserDatabase targetDb) {
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

    private Promise<Void> copyFormData(UserDatabase sourceDb, UserDatabase targetDb, final ExecutionContext context) {
        final List<Promise<VoidResult>> copyPromises = new ArrayList<>();

        for (Activity activity : sourceDb.getActivities()) {
            final Activity newActivity = copyActivity(activity, targetDb);

            final ResourceId sourceFormClass = CuidAdapter.activityFormClass(activity.getId());
            final ResourceId targetFormClass = CuidAdapter.activityFormClass(newActivity.getId());

            // form class
            copyPromises.add(copyFormClass(context, sourceFormClass, targetFormClass));

            // site form instances
            copyPromises.add(copySiteFormInstances(context, activity, newActivity));

        }
        return Promise.waitAll(copyPromises);
    }

    private Promise<VoidResult> copySiteFormInstances(final ExecutionContext context, Activity sourceActivity, final Activity targetActivity) {
        Filter filter = new Filter();
        filter.addRestriction(DimensionType.Activity, sourceActivity.getId());

        GetSites query = new GetSites();
        query.setFilter(filter);

        final Promise<ActivityFormDTO> activityForm = new Promise<>();
        context.execute(new GetActivityForm(sourceActivity.getId()), activityForm);

        final Promise<SiteResult> fetchSitesPromise = new Promise<>();
        context.execute(query, fetchSitesPromise);

        return Promise.waitAll(activityForm, fetchSitesPromise).join(new Function<Void, Promise<VoidResult>>() {
            @Nullable
            @Override
            public Promise<VoidResult> apply(@Nullable Void input) {

                for (SiteDTO site : fetchSitesPromise.get().getData()) {
                    SiteBinding binding = new SiteBinding(activityForm.get());

                    // adapt id and classId to targetActivity
                    FormInstance formInstance = binding.newInstance(site)
                            .setId(CuidAdapter.cuid(CuidAdapter.SITE_DOMAIN, targetActivity.getId()))
                            .setClassId(CuidAdapter.activityFormClass(targetActivity.getId()));

                    // persist
                    new SitePersister(new DispatchAdapter(context)).persist(formInstance);
                }
                return Promise.resolved(VoidResult.INSTANCE);
            }
        });
    }

    private Promise<VoidResult> copyFormClass(final ExecutionContext context, ResourceId sourceFormClass, final ResourceId targetFormClass) {
        final Promise<VoidResult> promise = new Promise<>();
        context.execute(new GetFormClass(sourceFormClass), new AsyncCallback<FormClassResult>() {
            @Override
            public void onFailure(Throwable caught) {
                LOGGER.log(Level.SEVERE, caught.getMessage(), caught);
                promise.onFailure(caught);
            }

            @Override
            public void onSuccess(FormClassResult result) {
                FormClass formClass = result.getFormClass();
                ResourceId oldId = formClass.getId();
                formClass.setId(targetFormClass);

                FormClassTrash.normalizeBuiltInFormClassFields(formClass, oldId);

                context.execute(new UpdateFormClass(formClass), promise);
            }
        });
        return promise;
    }

    private Activity copyActivity(Activity sourceActivity, UserDatabase targetDb) {
        Activity newActivity = new Activity(sourceActivity);
        newActivity.setDatabase(targetDb);
        ActivityPolicy policy = injector.getInstance(ActivityPolicy.class);
        policy.persist(newActivity);
        return newActivity;
    }

    private UserDatabase createDatabase(CloneDatabase command, User user) {
        UserDatabase db = new UserDatabase();
        db.setName(command.getName());
        db.setFullName(command.getDescription());
        db.setCountry(em.find(Country.class, command.getCountryId()));
        db.setOwner(user);

        em.persist(db);
        return db;
    }

    private static class DispatchAdapter extends AbstractDispatcher {

        private final ExecutionContext executionContext;

        private DispatchAdapter(ExecutionContext executionContext) {
            this.executionContext = executionContext;
        }

        @Override
        public <T extends CommandResult> void execute(Command<T> command, AsyncCallback<T> callback) {
            try {
                executionContext.execute(command, callback);
            } catch (Exception e) {
                callback.onFailure(e);
            }
        }
    }
}
