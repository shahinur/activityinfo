package org.activityinfo.migrator.filter;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.legacy.KeyGenerator;
import org.activityinfo.model.resource.ResourceId;

import java.util.Map;

public class LegacyIdStrategy implements IdStrategy {

    private Map<ResourceId, ResourceId> revertedIds = Maps.newHashMap();
    private KeyGenerator keyGenerator = new KeyGenerator();

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

    @Override
    public ResourceId mapToLegacyId(char domain, ResourceId resourceId) {
        ResourceId legacyId = revertedIds.get(resourceId);
        if(legacyId == null) {
            legacyId = CuidAdapter.cuid(domain, keyGenerator.generateInt());
            revertedIds.put(resourceId, legacyId);
        }
        return legacyId;
    }

    @Override
    public ResourceId mapToLegacyId(ResourceId id) {
        if(id.asString().equals("backupBlobId")) {
            return ResourceId.valueOf("#importedFrom");
        }
        Preconditions.checkState(revertedIds.containsKey(id), "Id %s has not yet been mapped", id.asString());
        return revertedIds.get(id);
    }
}
