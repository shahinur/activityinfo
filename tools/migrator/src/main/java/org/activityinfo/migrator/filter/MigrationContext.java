package org.activityinfo.migrator.filter;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;

import java.util.Map;

public class MigrationContext {

    private MigrationFilter filter;
    private IdStrategy idStrategy = new LegacyIdStrategy();
    private ResourceId rootId = Resources.ROOT_ID;
    private ResourceId geoDbOwnerId = Resources.ROOT_ID;

    private Map<Integer, Integer> databaseOwnerMap = Maps.newHashMap();
    private long sourceVersionMigrated;
    private int maxSnapshotCount = Integer.MAX_VALUE;

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

    public int getDatabaseOwnerUser(int databaseId) {
        return Preconditions.checkNotNull(databaseOwnerMap.get(databaseId), "db " + databaseId);
    }

    public void setDatabaseOwnerUser(int databaseId, int userId) {
        databaseOwnerMap.put(databaseId, userId);
    }

    public void setSourceVersionMigrated(long sourceVersionMigrated) {
        this.sourceVersionMigrated = sourceVersionMigrated;
    }


    public long getSourceVersionMigrated() {
        return sourceVersionMigrated;
    }

    public int getMaxSnapshotCount() {
        return maxSnapshotCount;
    }

    public void setMaxSnapshotCount(int maxSnapshotCount) {
        this.maxSnapshotCount = maxSnapshotCount;
    }
}
