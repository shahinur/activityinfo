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
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * One-to-one DTO of the
 * {@link org.activityinfo.server.database.hibernate.entity.Partner} domain
 * class.
 *
 * @author Alex Bertram
 */
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public final class PartnerDTO extends BaseModelData implements DTO, ProvidesKey {

    public static final int NAME_MAX_LENGTH = 255;

    public PartnerDTO() {

    }

    public PartnerDTO(int id, String name) {
        setId(id);
        setName(name);
    }

    public void setId(int id) {
        set("id", id);
    }

    @JsonProperty @JsonView(DTOViews.Schema.class)
    public int getId() {
        return (Integer) get("id");
    }

    @JsonProperty @JsonView(DTOViews.Schema.class)
    public String getName() {
        return get("name");
    }

    public void setName(String value) {
        set("name", value);
    }

    public void setFullName(String value) {
        set("fullName", value);
    }

    @JsonProperty @JsonView(DTOViews.Schema.class)
    public String getFullName() {
        return get("fullName");
    }

    public boolean isOperational() {
        return true;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (!(other instanceof PartnerDTO)) {
            return false;
        }

        PartnerDTO that = (PartnerDTO) other;

        return that.getId() == this.getId();
    }

    @Override
    public int hashCode() {
        if (get("id") == null) {
            return 0;
        } else {
            return getId();
        }
    }

    @Override
    public String getKey() {
        return "partner" + getId();
    }

}
