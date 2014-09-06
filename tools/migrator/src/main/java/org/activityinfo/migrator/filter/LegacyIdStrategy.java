package org.activityinfo.migrator.filter;

import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.ResourceId;

public class LegacyIdStrategy implements IdStrategy {
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
}
