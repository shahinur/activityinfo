package org.activityinfo.migrator.filter;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.legacy.KeyGenerator;
import org.activityinfo.model.resource.ResourceId;

import java.util.Map;

public class LegacyIdStrategy implements IdStrategy {

    private final IdStore persistentMap;
    private Map<ResourceId, ResourceId> revertedIds = Maps.newHashMap();
    private KeyGenerator keyGenerator = new KeyGenerator();

    public LegacyIdStrategy() {
        this(new NullIdStore());
    }

    public LegacyIdStrategy(IdStore idStore) {
        this.persistentMap = idStore;
    }

    @Override
    public ResourceId resourceId(char domain, int id) {
        return CuidAdapter.resourceId(domain, id);
    }

    @Override
    public ResourceId countryFormClassId() {
        return ResourceId.valueOf("_country");
    }

    @Override
    public ResourceId activityCategoryId(int databaseId, String categoryName) {
        return CuidAdapter.activityCategoryFolderId(databaseId, categoryName);
    }

    @Override
    public ResourceId partnerInstanceId(int databaseId, int partnerId) {
        return CuidAdapter.partnerInstanceId(databaseId, partnerId);
    }

    @Override
    public ResourceId geoDbId() {
        return ResourceId.valueOf("_geodb");
    }

    private Optional<ResourceId> getRevertedId(ResourceId cuid) {
        if(revertedIds.containsKey(cuid)) {
            return Optional.of(revertedIds.get(cuid));
        } else {
            return persistentMap.getNewId(cuid);
        }
    }

    @Override
    public ResourceId mapToLegacyId(char domain, ResourceId cuid) {
        Optional<ResourceId> storedId = getRevertedId(cuid);
        if(storedId.isPresent()) {
            return storedId.get();
        } else {
            ResourceId legacyId = CuidAdapter.cuid(domain, keyGenerator.generateInt());
            revertedIds.put(cuid, legacyId);
            persistentMap.putNewId(cuid, legacyId);
            return legacyId;
        }
    }

    @Override
    public ResourceId mapToLegacyId(ResourceId cuid) {
        if(cuid.asString().equals("backupBlobId")) {
            return ResourceId.valueOf("#importedFrom");
        }
        Optional<ResourceId> legacyId = getRevertedId(cuid);
        Preconditions.checkState(legacyId.isPresent(), "Id %s has not yet been mapped", cuid.asString());
        return legacyId.get();
    }
}
