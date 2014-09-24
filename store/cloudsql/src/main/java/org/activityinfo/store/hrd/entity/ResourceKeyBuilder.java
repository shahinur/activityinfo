package org.activityinfo.store.hrd.entity;
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

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.common.base.Preconditions;
import org.activityinfo.model.resource.ResourceId;

/**
 * Builds resource key. Key must contain full path to root (in our case workspace) key.
 *
 * @author yuriyz on 9/24/14.
 */
public class ResourceKeyBuilder {

    private final WorkspaceTransaction transaction;
    private final boolean resourcePersisted;

    private String kind;
    private ResourceId resourceId;
    private ResourceId parentResourceId;
    private ResourceId workspaceId;

    public ResourceKeyBuilder(WorkspaceTransaction transaction, boolean resourcePersisted) {
        this.transaction = transaction;
        this.resourcePersisted = resourcePersisted;
    }

    public ResourceKeyBuilder setKind(String kind) {
        this.kind = kind;
        return this;
    }

    public ResourceKeyBuilder setResourceId(ResourceId resourceId) {
        this.resourceId = resourceId;
        return this;
    }

    public ResourceKeyBuilder setParentResourceId(ResourceId parentResourceId) {
        this.parentResourceId = parentResourceId;
        return this;
    }

    public ResourceKeyBuilder setWorkspaceId(ResourceId workspaceId) {
        this.workspaceId = workspaceId;
        return this;
    }

    public Key build() {
        Preconditions.checkNotNull(kind);
        Preconditions.checkNotNull(resourceId);
        Preconditions.checkNotNull(workspaceId);

        final Key workspaceKey = KeyFactory.createKey(Workspace.ROOT_KIND, workspaceId.asString());
        if (workspaceId.equals(parentResourceId)) { // we are lucky, workspace is direct parent
            return workspaceKey;
        }

        if (resourcePersisted) {
            return buildPersistedKey(workspaceKey);
        } else {
            return buildNotPersistedKey(workspaceKey);
        }
    }

    private Key buildPersistedKey(Key workspaceKey) {
        Query query = new Query(kind)
                .setAncestor(workspaceKey)
                .setFilter(new Query.FilterPredicate(LatestContent.RESOURCE_ID_PROPERTY,
                        Query.FilterOperator.EQUAL, resourceId.asString()));

        return transaction.prepare(query).asSingleEntity().getKey();
    }

    private Key buildNotPersistedKey(Key workspaceKey) {
        Query query = new Query(kind)
                .setAncestor(workspaceKey)
                .setFilter(new Query.FilterPredicate(LatestContent.RESOURCE_ID_PROPERTY,
                        Query.FilterOperator.EQUAL, parentResourceId.asString()));

        Entity entity = transaction.prepare(query).asSingleEntity();

        Key parentKey = entity.getKey();
        Preconditions.checkNotNull(parentKey);

        return KeyFactory.createKey(parentKey, kind, resourceId.asString());
    }


}
