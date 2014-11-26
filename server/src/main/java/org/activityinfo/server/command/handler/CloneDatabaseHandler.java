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

import com.google.api.client.util.Maps;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.activityinfo.legacy.client.remote.AbstractDispatcher;
import org.activityinfo.legacy.shared.command.CloneDatabase;
import org.activityinfo.legacy.shared.command.Command;
import org.activityinfo.legacy.shared.command.GetFormClass;
import org.activityinfo.legacy.shared.command.UpdateFormClass;
import org.activityinfo.legacy.shared.command.result.CommandResult;
import org.activityinfo.legacy.shared.command.result.CreateResult;
import org.activityinfo.legacy.shared.command.result.FormClassResult;
import org.activityinfo.legacy.shared.command.result.VoidResult;
import org.activityinfo.legacy.shared.exception.IllegalAccessCommandException;
import org.activityinfo.legacy.shared.impl.CommandHandlerAsync;
import org.activityinfo.legacy.shared.impl.ExecutionContext;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.legacy.KeyGenerator;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.promise.Promise;
import org.activityinfo.server.database.hibernate.entity.*;
import org.activityinfo.server.endpoint.gwtrpc.RemoteExecutionContext;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author yuriyz on 11/17/2014.
 */
public class CloneDatabaseHandler implements CommandHandlerAsync<CloneDatabase, CreateResult> {

    private static final Logger LOGGER = Logger.getLogger(CloneDatabaseHandler.class.getName());

    private final EntityManager em;
    private final PermissionOracle permissionOracle;
    private final KeyGenerator generator = new KeyGenerator();

    // Mappings old id (source db) -> new id (target/newly created db)
    private final Map<Integer, Partner> partnerMapping = Maps.newHashMap();
    private final Map<Integer, Activity> activityMapping = Maps.newHashMap();
    private final Map<Integer, AttributeGroup> attributeGroupMapping = Maps.newHashMap();

    @Inject
    public CloneDatabaseHandler(Injector injector) {
        this.em = injector.getInstance(EntityManager.class);
        this.permissionOracle = injector.getInstance(PermissionOracle.class);
    }

    @Override
    public void execute(CloneDatabase command, ExecutionContext context, final AsyncCallback<CreateResult> callback) {

        final User user = ((RemoteExecutionContext) context).retrieveUserEntity();
        final UserDatabase targetDb = createDatabase(command, user);

        UserDatabase sourceDb = em.find(UserDatabase.class, command.getSourceDatabaseId());

        if (!permissionOracle.isDesignAllowed(sourceDb, user)) {
            throw new IllegalAccessCommandException();
        }

        // if the new countryId of the target database is different than the countryId of sourceDatabase,
        // copyData must be false -> skip copy
        if (sourceDb.getCountry().getId() != targetDb.getCountry().getId()) {
            callback.onSuccess(new CreateResult(targetDb.getId()));
            return;
        }

        // 1. copy partners and keep mapping between old and new partners
        if (command.isCopyPartners() || command.isCopyUsers()) {
            copyPartners(sourceDb, targetDb);
        }

        // 2. copy user permissions
        if (command.isCopyUsers()) {
            copyUserPermissions(sourceDb, targetDb, user);
        }

        List<Promise<Void>> promises = new ArrayList<>();

        // 3. copy form data
        if (command.isCopyData()) {
            promises.add(copyFormData(sourceDb, targetDb, context));
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

            // set newly created partner
            if (sourcePermission.getPartner() != null) {
                Partner targetPartner = partnerMapping.get(sourcePermission.getPartner().getId());
                newPermission.setPartner(targetPartner != null ? targetPartner : null);
            }

            em.persist(newPermission);

        }
    }

    private void copyPartners(UserDatabase sourceDb, UserDatabase targetDb) {
        for (Partner partner : sourceDb.getPartners()) {
            Partner newPartner = new Partner();
            newPartner.setName(partner.getName());
            newPartner.setFullName(partner.getFullName());

            em.persist(newPartner);

            partnerMapping.put(partner.getId(), newPartner);
            targetDb.getPartners().add(newPartner);
        }

        targetDb.setLastSchemaUpdate(new Date());
        em.persist(targetDb);
    }

    private Promise<Void> copyFormData(UserDatabase sourceDb, UserDatabase targetDb, final ExecutionContext context) {

        // first copy all activities without payload (indicators, attributes)
        for (Activity activity : sourceDb.getActivities()) {
            copyActivity(activity, targetDb);
        }

        // copy activity payload (indicators, attributes)
        for (Activity activity : sourceDb.getActivities()) {
            copyActivityPayload(activity, targetDb);
        }

        // we have to commit in order to get valid FormClass
        // hack : start own transaction, can't wait on RemoteExecutionContext
        // REASON : we have to commit transaction internally in handler
        // in order to get valid FormClass while still inside handler and perform GetFormClass and
        // then push it to activity via UpdateFormClass
        try {
            em.flush();
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        }

        // make RemoteExecutionContext happy and allow him to commit()
        em.getTransaction().begin();

        final List<Promise<VoidResult>> copyPromises = new ArrayList<>();
        for (Activity activity : sourceDb.getActivities()) {
            final ResourceId sourceFormClass = CuidAdapter.activityFormClass(activity.getId());
            final ResourceId targetFormClass = CuidAdapter.activityFormClass(activityMapping.get(activity.getId()).getId());

            // copy form class of newActivity
            copyPromises.add(copyFormClass(context, targetFormClass));

            // site form instances
            // todo : commenting it temporary until we have nice idea how to implement it in scalable manner. (AI-787)
//            copyPromises.add(copySiteFormInstances(context, activity, newActivity));
        }
        return Promise.waitAll(copyPromises);
    }

//    private Promise<VoidResult> copySiteFormInstances(final ExecutionContext context, Activity sourceActivity, final Activity targetActivity) {
//        Filter filter = new Filter();
//        filter.addRestriction(DimensionType.Activity, sourceActivity.getId());
//
//        GetSites query = new GetSites();
//        query.setFilter(filter);
//
//        final Promise<ActivityFormDTO> activityForm = new Promise<>();
//        context.execute(new GetActivityForm(sourceActivity.getId()), activityForm);
//
//        final Promise<SiteResult> fetchSitesPromise = new Promise<>();
//        context.execute(query, fetchSitesPromise);
//
//        return Promise.waitAll(activityForm, fetchSitesPromise).join(new Function<Void, Promise<VoidResult>>() {
//            @Nullable
//            @Override
//            public Promise<VoidResult> apply(@Nullable Void input) {
//
//                for (SiteDTO site : fetchSitesPromise.get().getData()) {
//                    SiteBinding binding = new SiteBinding(activityForm.get());
//
//                    // adapt id and classId to targetActivity
//                    FormInstance formInstance = binding.newInstance(site)
//                            .setId(CuidAdapter.cuid(CuidAdapter.SITE_DOMAIN, targetActivity.getId()))
//                            .setClassId(CuidAdapter.activityFormClass(targetActivity.getId()));
//
//                    // persist
//                    new SitePersister(new DispatchAdapter(context)).persist(formInstance);
//                }
//                return Promise.resolved(VoidResult.INSTANCE);
//            }
//        });
//    }

    private Promise<VoidResult> copyFormClass(final ExecutionContext context, final ResourceId targetFormClass) {
        final Promise<VoidResult> promise = new Promise<>();
        context.execute(new GetFormClass(targetFormClass), new AsyncCallback<FormClassResult>() {
            @Override
            public void onFailure(Throwable caught) {
                LOGGER.log(Level.SEVERE, caught.getMessage(), caught);
                promise.onFailure(caught);
            }

            @Override
            public void onSuccess(FormClassResult result) {
                UpdateFormClass updateFormClass = new UpdateFormClass(result.getFormClass())
                        .setSyncActivityEntities(false);
                context.execute(updateFormClass, promise);
            }
        });
        return promise;
    }

    private Activity copyActivity(Activity sourceActivity, UserDatabase targetDb) {
        Activity newActivity = new Activity(sourceActivity); // copy simple values : like name, category (but not Indicators, Attributes)
        newActivity.getAttributeGroups().clear();
        newActivity.getLockedPeriods().clear();
        newActivity.getIndicators().clear();

        // target db
        newActivity.setDatabase(targetDb);

        em.persist(newActivity); // persist to get id of new activity
        activityMapping.put(sourceActivity.getId(), newActivity);

        return newActivity;
    }


    private Activity copyActivityPayload(Activity sourceActivity, UserDatabase targetDb) {
        Activity newActivity = activityMapping.get(sourceActivity.getId());

        // copy indicators
        for (Indicator indicator : sourceActivity.getIndicators()) {
            Indicator newIndicator = copyIndicator(indicator, newActivity);
            newActivity.getIndicators().add(newIndicator);
        }

        // copy attribute groups
        for (AttributeGroup attributeGroup : sourceActivity.getAttributeGroups()) {
            AttributeGroup newAttributeGroup = copyAttributeGroup(attributeGroup);

            newActivity.getAttributeGroups().add(newAttributeGroup);
        }

        // copy locked periods
        for (LockedPeriod lockedPeriod : sourceActivity.getLockedPeriods()) {
            LockedPeriod newLockedPeriod = new LockedPeriod(lockedPeriod);
            newLockedPeriod.setActivity(newActivity);
            newLockedPeriod.setUserDatabase(targetDb);
            em.persist(newLockedPeriod);
        }

        em.persist(newActivity);
        return newActivity;
    }

    private AttributeGroup copyAttributeGroup(AttributeGroup sourceAttributeGroup) {
        AttributeGroup newAttributeGroup = attributeGroupMapping.get(sourceAttributeGroup.getId());
        if (newAttributeGroup != null) {
            return newAttributeGroup;
        }

        newAttributeGroup = new AttributeGroup(sourceAttributeGroup);
        newAttributeGroup.getAttributes().clear();
        newAttributeGroup.getActivities().clear();
        newAttributeGroup.setId(generator.generateInt());

        // activity references
        for (Activity sourceActivity : sourceAttributeGroup.getActivities()) {
            Activity newActivity = activityMapping.get(sourceActivity.getId());
            if (newActivity != null) {
                newActivity.getAttributeGroups().add(newAttributeGroup);
            }
        }

        em.persist(newAttributeGroup);
        attributeGroupMapping.put(sourceAttributeGroup.getId(), newAttributeGroup);

        // copy attributes
        for (Attribute attribute : sourceAttributeGroup.getAttributes()) {
            Attribute newAttribute = new Attribute(attribute);
            newAttribute.setId(generator.generateInt());
            newAttribute.setGroup(newAttributeGroup);

            em.persist(newAttribute);
            newAttributeGroup.getAttributes().add(newAttribute);
        }


        return newAttributeGroup;
    }

    private Indicator copyIndicator(Indicator indicator, Activity newActivity) {
        Indicator newIndicator = new Indicator(indicator);
        newIndicator.setId(generator.generateInt());

        newIndicator.setActivity(newActivity);

        em.persist(newIndicator);
        return newIndicator;
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
