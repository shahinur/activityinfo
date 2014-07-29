package org.activityinfo.migrator.globals;

import org.activityinfo.model.resource.Resource;
import org.activityinfo.migrator.ResourceMigrator;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

public abstract  class SingletonMigrator extends ResourceMigrator {

    @Override
    public final List<Resource> getResources(Connection connection) {
        return Arrays.asList(getResource());
    }

    protected abstract Resource getResource();

}
