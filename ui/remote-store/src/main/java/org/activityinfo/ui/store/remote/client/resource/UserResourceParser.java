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
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
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
        return parseUserResource(new JSONObject(object));
    }

    public static UserResource parseUserResource(JSONObject object) {
        UserResource userResource = UserResource.userResource();
        JSONBoolean editAllowed = object.get("@editAllowed").isBoolean();
        if (editAllowed != null) {
            userResource.setEditAllowed(editAllowed.booleanValue());
        }
        JSONBoolean isOwner = object.get("@owner").isBoolean();
        if (isOwner != null) {
            userResource.setOwner(isOwner.booleanValue());
        }
        JSONObject hasResource = object.get("@resource").isObject();
        if (hasResource != null) {
            userResource.setResource(ResourceParser.parseResource(hasResource.getJavaScriptObject()));
        }
        return userResource;
    }
}
