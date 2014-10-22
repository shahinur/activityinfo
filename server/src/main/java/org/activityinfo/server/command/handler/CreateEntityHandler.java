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

import com.google.inject.Inject;
import org.activityinfo.legacy.shared.command.CreateEntity;
import org.activityinfo.legacy.shared.command.result.CommandResult;
import org.activityinfo.legacy.shared.command.result.CreateResult;
import org.activityinfo.legacy.shared.exception.CommandException;
import org.activityinfo.server.command.handler.adapter.SchemaUpdater;
import org.activityinfo.server.database.hibernate.entity.User;
import org.activityinfo.service.store.ResourceStore;

public class CreateEntityHandler implements CommandHandler<CreateEntity> {

    private final ResourceStore store;

    @Inject
    public CreateEntityHandler(ResourceStore store) {
        this.store = store;
    }


    @Override
    public CommandResult execute(CreateEntity cmd, User user) throws CommandException {
        int newId = new SchemaUpdater(store, user.asAuthenticatedUser()).create(cmd);
        return new CreateResult(newId);
    }
//
//    private class Update {
//
//        private AuthenticatedUser user;
//
//        private Update(AuthenticatedUser user) {
//            this.user = user;
//        }
//
//        public void create
//
//    }
//
//    private void createDatabase(AuthenticatedUser user, Map<String, Object> properties) {
//        Folder folder = new Folder();
//
//        folder.setLabel((String)properties.get(UserDatabaseDTO.NAME_PROPERTY));
//        folder.setDescription((String)properties.get(UserDatabaseDTO.FULL_NAME_PROPERTY));
//
//        Resource workspace = Resources.createResource();
//        workspace.setId(CuidAdapter.databaseId(keyGenerator.generateInt()));
//        workspace.setOwnerId(Resources.ROOT_ID);
//        store.create(user, workspace);
//    }
//
//    private void createLocationType(User user, CreateEntity cmd, Map<String, Object> properties) {
//        throw new UnsupportedOperationException();
//    }
//
//    private CommandResult createAttributeGroup(CreateEntity cmd, Map<String, Object> properties) {
//
//
//
//        Activity activity = entityManager().find(Activity.class, properties.get("activityId"));
//
//        AttributeGroup group = new AttributeGroup();
//        group.setSortOrder(activity.getAttributeGroups().size() + 1);
//        updateAttributeGroupProperties(group, properties);
//
//        entityManager().persist(group);
//
//        activity.getAttributeGroups().add(group);
//
//        activity.getDatabase().setLastSchemaUpdate(new Date());
//
//
//        return new CreateResult(group.getId());
//    }
//
//    private CommandResult createAttribute(CreateEntity cmd, Map<String, Object> properties) {
//        Attribute attribute = new Attribute();
//        AttributeGroup ag = entityManager().getReference(AttributeGroup.class, properties.get("attributeGroupId"));
//        attribute.setGroup(ag);
//
//        updateAttributeProperties(properties, attribute);
//
//        Activity activity = ag.getActivities().iterator().next(); // Assume
//        // group has
//        // only one
//        // activity
//
//        entityManager().persist(attribute);
//        activity.getDatabase().setLastSchemaUpdate(new Date());
//
//        attribute.setSortOrder(ag.getAttributes().size());
//
//        return new CreateResult(attribute.getId());
//    }
//
//    private CommandResult createIndicator(User user,
//                                          CreateEntity cmd,
//                                          Map<String, Object> properties) throws IllegalAccessCommandException {
//
//        Indicator indicator = new Indicator();
//        Activity activity = entityManager().getReference(Activity.class, properties.get("activityId"));
//        indicator.setActivity(activity);
//
//        assertDesignPrivileges(user, indicator.getActivity().getDatabase());
//
//        updateIndicatorProperties(indicator, properties);
//
//        entityManager().persist(indicator);
//        activity.getDatabase().setLastSchemaUpdate(new Date());
//
//        return new CreateResult(indicator.getId());
//    }
}
