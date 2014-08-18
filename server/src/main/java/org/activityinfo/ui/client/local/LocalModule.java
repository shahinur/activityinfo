package org.activityinfo.ui.client.local;

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

import com.bedatadriven.rebar.sql.client.SqlDatabase;
import com.bedatadriven.rebar.sql.client.SqlException;
import com.bedatadriven.rebar.sql.client.SqlTransactionCallback;
import com.bedatadriven.rebar.sql.client.query.SqlDialect;
import com.bedatadriven.rebar.sql.client.query.SqliteDialect;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.activityinfo.model.auth.AuthenticatedUser;

public class LocalModule extends AbstractGinModule {

    @Override
    protected void configure() {

    }

    @Provides @Singleton
    protected SqlDatabase provideSqlDatabase(AuthenticatedUser auth) {

        return new NullDatabase();
    }

    private static class NullDatabase extends SqlDatabase {

        @Override
        public void transaction(SqlTransactionCallback callback) {
            callback.onError(new SqlException("Database could not be opened"));
        }

        @Override
        public SqlDialect getDialect() {
            return new SqliteDialect();
        }

        @Override
        public void executeUpdates(String bulkOperationJsonArray, AsyncCallback<Integer> callback) {
            callback.onFailure(new SqlException("Database could not be opened."));
        }

        @Override
        public String getName() {
            return "nulldb";
        }
    }
}
