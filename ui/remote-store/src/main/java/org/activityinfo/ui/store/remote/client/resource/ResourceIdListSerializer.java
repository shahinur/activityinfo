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

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONString;
import org.activityinfo.model.resource.ResourceId;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author yuriyz on 10/1/14.
 */
public class ResourceIdListSerializer {

    private ResourceIdListSerializer() {
    }

    public static String toJson(Collection<ResourceId> resourceIds) {
        JSONArray jsonArray = new JSONArray();
        Iterator<ResourceId> iterator = resourceIds.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            jsonArray.set(index, new JSONString(iterator.next().asString()));
            index++;
        }
        return jsonArray.toString();
    }
}
