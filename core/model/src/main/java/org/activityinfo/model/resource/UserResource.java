package org.activityinfo.model.resource;
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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;

/**
 * Contains resource together with information related to User entity (Resource<->User).
 *
 * For now such information is permissions (E.g. "Is user allowed to edit this resource?").
 *
 * @author yuriyz on 9/25/14.
 */
public class UserResource {

    @Nullable
    private Resource resource;

    private Boolean editAllowed = false;
    private Boolean owner = false;

    public UserResource() {
    }

    public static UserResource userResource() {
        return new UserResource();
    }

    public static UserResource userResource(Resource resource) {
        return userResource().setResource(resource);
    }

    @JsonCreator
    public UserResource(@JsonProperty("resource") Resource resource,
                        @JsonProperty("editAllowed") Boolean editAllowed,
                        @JsonProperty("owner") Boolean owner) {
        this.resource = resource;
        this.editAllowed = editAllowed;
        this.owner = owner;
    }

    public UserResource copy() {
        UserResource copy = new UserResource();
        copy.resource = this.resource != null ? this.resource.copy() : null;
        copy.editAllowed = this.editAllowed;
        copy.owner = this.owner;
        return copy;
    }

    public ResourceId getResourceId() {
        return this.resource.getId();
    }

    /**
     * Returns the resource.
     *
     * @return the resource
     */
    public Resource getResource() {
        return resource;
    }

    public Boolean getEditAllowed() {
        return editAllowed;
    }

    public Boolean isOwner() {
        return owner;
    }

    public UserResource setOwner(Boolean owner) {
        this.owner = owner;
        return this;
    }

    public UserResource setResource(@Nullable Resource resource) {
        this.resource = resource;
        return this;
    }

    /**
     * Sets "edit allowed" permission.
     *
     * @param editAllowed edit allowed flag
     * @return "edit allowed" permission.
     */
    public UserResource setEditAllowed(Boolean editAllowed) {
        this.editAllowed = editAllowed;
        return this;
    }
}
