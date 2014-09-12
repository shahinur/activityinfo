package org.activityinfo.migrator.filter;

import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;

public class MigrationContext {

    private MigrationFilter filter;
    private IdStrategy idStrategy = new LegacyIdStrategy();
    private ResourceId rootId = Resources.ROOT_ID;
    private ResourceId geoDbOwnerId = Resources.ROOT_ID;

    public MigrationContext(MigrationFilter filter) {
        this.filter = filter;
    }

    public MigrationContext(IdStrategy strategy, MigrationFilter filter) {
        this.idStrategy = strategy;
        this.filter = filter;
    }

    public ResourceId getRootId() {
        return rootId;
    }

    public void setRootId(ResourceId rootId) {
        this.rootId = rootId;
    }

    public ResourceId getGeoDbOwnerId() {
        return geoDbOwnerId;
    }

    public void setGeoDbOwnerId(ResourceId geoDbOwnerId) {
        this.geoDbOwnerId = geoDbOwnerId;
    }

    public MigrationFilter filter() {
        return filter;
    }

    public ResourceId resourceId(char domain, int legacyId) {
        return idStrategy.resourceId(domain, legacyId);
    }

    public IdStrategy getIdStrategy() {
        return idStrategy;
    }
}
