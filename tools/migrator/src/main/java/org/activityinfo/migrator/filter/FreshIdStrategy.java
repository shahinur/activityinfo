package org.activityinfo.migrator.filter;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;

import java.util.Map;

/**
 * Assigns all resources completely new but consistent ids
 */
public class FreshIdStrategy implements IdStrategy {

    private Map<String, ResourceId> map = Maps.newHashMap();

    public FreshIdStrategy() {
    }

    @Override
    public ResourceId resourceId(char domain, int id) {
        return getOrCreateId(String.valueOf(domain) + id);
    }

    @Override
    public ResourceId activityCategoryId(int databaseId, String categoryName) {
        return getOrCreateId(String.valueOf(CuidAdapter.ACTIVITY_CATEGORY_DOMAIN) + databaseId + "_" + categoryName);
    }

    @Override
    public ResourceId partnerInstanceId(int databaseId, int partnerId) {
        return getOrCreateId(String.valueOf(CuidAdapter.PARTNER_DOMAIN) + databaseId + "p" + partnerId);
    }

    @Override
    public ResourceId geoDbId() {
        return getOrCreateId("_geodb");
    }

    @Override
    public ResourceId mapToLegacyId(char domain, ResourceId resourceId) {
        return getOrCreateId(resourceId.asString());
    }

    @Override
    public ResourceId mapToLegacyId(ResourceId id) {
        Preconditions.checkState(map.containsKey(id.asString()), "Id %s has not been mapped yet", id.asString());
        return map.get(id.asString());
    }

    @Override
    public ResourceId countryFormClassId() {
        return getOrCreateId("_country");
    }

    private ResourceId getOrCreateId(String key) {
        ResourceId resourceId = map.get(key);
        if(resourceId == null) {
            resourceId = Resources.generateId();
            map.put(key, resourceId);
        }
        return resourceId;
    }

}
