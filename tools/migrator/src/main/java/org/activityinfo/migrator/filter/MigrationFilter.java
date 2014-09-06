package org.activityinfo.migrator.filter;

public interface MigrationFilter {

    String adminLevelFilter(String adminLevelTableAlias);

    String activityFilter(String activityTableAlias);

    String adminEntityFilter();

    String countryFilter();

    String locationFilter();

    String locationTypeFilter(String locationTypeTableAlias);

    String databaseFilter();

    String partnerFilter(String partnerTableAlias);

    String projectFilter();

    String siteFilter(String siteTableAlias);

    String indicatorFilter(String indicatorTableAlias);

    String attributeGroupFilter(String attributeGroupInActivityTableAlias);

    String attributeFilter(String attributeGroupTableAlias);
}
