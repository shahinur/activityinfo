package org.activityinfo.ui.store.remote.client.resource;
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
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Response;
import org.activityinfo.model.resource.UserResource;

import javax.annotation.Nullable;

/**
 * @author yuriyz on 9/26/14.
 */
public class UserResourceParser implements Function<Response, UserResource> {
    @Nullable
    @Override
    public UserResource apply(@Nullable Response input) {
        return parse(input.getText());
    }

    public static UserResource parse(String json) {
        JavaScriptObject object = JsonUtils.safeEval(json);
        return parseResource(object);
    }

    public static UserResource parseResource(JavaScriptObject object) {
        UserResource userResource = UserResource.userResource();

        // todo
//        userResource.setResource(ResourceParser.parseResource());
//        userResource.setEditAllowed();
//        userResource.setOwner();
//        parseResourceProperties(resource, object);
//        resource.setValue(new RecordJsoImpl(new JSONObject(object)));
        return userResource;
    }
}
