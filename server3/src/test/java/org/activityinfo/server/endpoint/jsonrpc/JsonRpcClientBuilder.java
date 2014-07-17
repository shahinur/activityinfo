package org.activityinfo.server.endpoint.jsonrpc;
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
 * @author yuriyz on 6/24/14.
 */
public class JsonRpcClientBuilder {

    private String endpoint;
    private String username;
    private String password;

    public JsonRpcClientBuilder() {
    }

    public static JsonRpcClientBuilder builder() {
        return new JsonRpcClientBuilder();
    }

    public JsonRpcClientBuilder endpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public JsonRpcClientBuilder username(String username) {
        this.username = username;
        return this;
    }

    public JsonRpcClientBuilder password(String password) {
        this.password = password;
        return this;
    }

    public JsonRpcClient build() {
        return new JsonRpcClient(endpoint, username, password);
    }
}
