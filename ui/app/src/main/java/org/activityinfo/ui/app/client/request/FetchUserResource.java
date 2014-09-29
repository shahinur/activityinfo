package org.activityinfo.ui.app.client.request;
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

import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.UserResource;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.store.RemoteStoreService;

/**
 * @author yuriyz on 9/26/14.
 */
public class FetchUserResource implements Request<UserResource> {

    private ResourceId resourceId;

    public FetchUserResource(ResourceId resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public Promise<UserResource> send(RemoteStoreService service) {
        return service.getUserResource(resourceId);
    }
}
