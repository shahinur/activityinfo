package org.activityinfo.server.command.handler.sync;

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

import com.google.gson.stream.JsonWriter;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SqliteBatchBuilder {

    private static final Logger LOGGER = Logger.getLogger(SqliteBatchBuilder.class.getName());

    private StringWriter stringWriter;
    private JsonWriter jsonWriter;

    public SqliteBatchBuilder() throws IOException {
        super();
        this.stringWriter = new StringWriter();
        this.jsonWriter = new JsonWriter(stringWriter);
        this.jsonWriter.beginArray();
    }

    public void addStatement(String sqlState) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("statement");
        jsonWriter.value(sqlState);
        jsonWriter.endObject();

        if(sqlState.length() > 1024) {
            LOGGER.log(Level.WARNING, "Add statement with size " + sqlState.length() + ": " +
                    sqlState.substring(0, 100) + "...");
        }
    }

    public SqliteInsertBuilder insert() {
        return new SqliteInsertBuilder(this);
    }

    public void createTableIfNotExists(EntityManager em, String tableName) {
        new SqliteCreateTableBuilder(this, tableName).execute(em);
    }

    public void createTablesIfNotExist(EntityManager em, String... tableNames) {
        for(String tableName : tableNames) {
            createTableIfNotExists(em, tableName);
        }
    }

    public String build() throws IOException {
        jsonWriter.endArray();
        jsonWriter.flush();
        return stringWriter.toString();
    }

    public SqliteDeleteBuilder delete() {
        return new SqliteDeleteBuilder(this);
    }

    public void clearTables(String... tables) throws IOException {
        for(String table : tables) {
            addStatement("DELETE FROM " + table);
        }
    }
}
