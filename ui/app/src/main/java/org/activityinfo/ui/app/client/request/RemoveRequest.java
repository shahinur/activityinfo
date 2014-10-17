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

import org.activityinfo.client.ActivityInfoAsyncClient;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.store.UpdateResult;

/**
 * @author yuriyz on 9/22/14.
 */
public class RemoveRequest implements Request<UpdateResult> {

    private ResourceId resource;

    public RemoveRequest(ResourceId resource) {
        this.resource = resource;
    }

    public ResourceId getResource() {
        return resource;
    }

    @Override
    public Promise<UpdateResult> send(ActivityInfoAsyncClient service) {
        return service.remove(resource);
    }
}
