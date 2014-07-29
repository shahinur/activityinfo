package org.activityinfo.migrator;

import org.activityinfo.model.resource.Resource;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class ResourceMigrator {

    public abstract Iterable<Resource> getResources(Connection connection) throws SQLException;
}
