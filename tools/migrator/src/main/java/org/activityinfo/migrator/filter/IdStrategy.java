package org.activityinfo.migrator.filter;

import org.activityinfo.model.resource.ResourceId;

public interface IdStrategy {

    ResourceId resourceId(char domain, int id);

    ResourceId activityCategoryId(int databaseId, String categoryName);

    ResourceId countryFormClassId();

    ResourceId partnerInstanceId(int databaseId, int partnerId);

    ResourceId geoDbId();
}
