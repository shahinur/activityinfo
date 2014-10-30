package org.activityinfo.server.authentication;
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

/**
 * @author yuriyz on 10/29/2014.
 */
public class UserNoAuthEntity {

    private String secureToken;
    private long userId;
    private boolean subscribe;

    public UserNoAuthEntity() {
    }

    public UserNoAuthEntity(String secureToken, long userId, boolean subscribe) {
        this.secureToken = secureToken;
        this.userId = userId;
        this.subscribe = subscribe;
    }

    public String getSecureToken() {
        return secureToken;
    }

    public UserNoAuthEntity setSecureToken(String secureToken) {
        this.secureToken = secureToken;
        return this;
    }

    public long getUserId() {
        return userId;
    }

    public UserNoAuthEntity setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public boolean isSubscribe() {
        return subscribe;
    }

    public UserNoAuthEntity setSubscribe(boolean subscribe) {
        this.subscribe = subscribe;
        return this;
    }
}
