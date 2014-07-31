package org.activityinfo.migrator;

import java.sql.Connection;

public abstract class ResourceMigrator {

    public abstract void getResources(Connection connection, ResourceWriter writer) throws Exception;
}
