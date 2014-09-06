package org.activityinfo.migrator.filter;

public enum GeoDbStrategy {
    /**
     * Migrates the Country, AdminEntity, Location, and LocationType tables
     * to a global "Geodb" workspace
     */
    GLOBAL,

    /**
     * Copies filtered geographic reference data into the workspace.
     */
    COPY
}
