package org.activityinfo.legacy.shared.model;

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

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gson.JsonObject;
import org.activityinfo.legacy.shared.util.JsonUtil;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

public final class SiteHistoryDTO extends BaseModelData {
    private static final long serialVersionUID = 4951231064643225215L;

    public static final String ENTITY_NAME = "SiteHistory";

    public SiteHistoryDTO() {
    }

    public String getUserName() {
        return get("userName");
    }

    public void setUserName(String userName) {
        set("userName", userName);
    }

    public String getUserEmail() {
        return get("userEmail");
    }

    public void setUserEmail(String userEmail) {
        set("userEmail", userEmail);
    }

    public boolean isInitial() {
        return get("initial", false);
    }

    public void setInitial(boolean initial) {
        set("initial", initial);
    }

    public String getJson() {
        return get("json");
    }

    public JsonObject getJsonObject() {
        return JsonUtil.parse(getJson());
    }

    public Map<String, Object> getJsonMap() {
        return Collections.emptyMap();
    }

    public void setJson(String json) {
        set("json", json);
    }

    public long getTimeCreated() {
        return (Long) get("timeCreated");
    }

    public Date getDateCreated() {
        return new Date(getTimeCreated());
    }

    public void setTimeCreated(long timeCreated) {
        set("timeCreated", timeCreated);
    }

    public void setTimeCreated(double timeCreated) {
        setTimeCreated((long) timeCreated);
    }

    public SiteHistoryDTO copy() {
        SiteHistoryDTO copy = new SiteHistoryDTO();
        copy.setProperties(this.getProperties());
        return copy;
    }
}
