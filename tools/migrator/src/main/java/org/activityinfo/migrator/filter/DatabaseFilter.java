package org.activityinfo.migrator.filter;

public class DatabaseFilter implements MigrationFilter {

    private int databaseId;
    private int countryId;

    public DatabaseFilter(int databaseId, int countryId) {
        this.databaseId = databaseId;
        this.countryId = countryId;
    }

    @Override
    public String adminLevelFilter(String adminLevelTableAlias) {
        return adminLevelTableAlias + ".countryId = " + countryId;
    }

    @Override
    public String activityFilter(String activityTableAlias) {
        return activityTableAlias + ".databaseId = " + databaseId;
    }

    @Override
    public String adminEntityFilter() {
        return "adminLevelId in (select adminlevelid from adminlevel where " + adminLevelFilter("adminlevel") + ")";
    }

    @Override
    public String countryFilter() {
        return "countryId = " + countryId;
     }

    @Override
    public String locationFilter() {
        return "locationTypeId in (select locationtypeid from locationtype LT where " + locationTypeFilter("LT") + ")";
    }

    @Override
    public String locationTypeFilter(String locationTypeTableAlias) {
        return locationTypeTableAlias + ".locationTypeId in" +
            " (select locationtypeid from activity A WHERE A.datedeleted is null and " + activityFilter("A") + ")";
    }

    @Override
    public String databaseFilter() {
        return "databaseId = " + databaseId;
    }

    @Override
    public String partnerFilter(String partnerTableAlias) {
        return partnerTableAlias + ".partnerId in " +
            "(select partnerid from partnerindatabase where databaseid = " + databaseId + ")";
    }

    @Override
    public String projectFilter() {
        return "databaseId = " + databaseId;
    }

    @Override
    public String siteFilter(String siteTableAlias) {
        return siteTableAlias + ".activityId in " + activityQuery();
    }

    private String activityQuery() {
        return "(select activityid from activity where " + databaseFilter() + ")";
    }

    @Override
    public String attributeGroupFilter(String attributeGroupInActivityTableAlias) {
        return attributeGroupInActivityTableAlias + ".activityId in " + activityQuery();
    }

    @Override
    public String attributeFilter(String attributeGroupTableAlias) {
        return attributeGroupTableAlias + ".AttributeGroupId in " +
            "(select attributegroupid from attributegroupinactivity A WHERE " +
                    attributeGroupFilter("A") + ")";

    }

    @Override
    public String indicatorFilter(String indicatorTableAlias) {
        return indicatorTableAlias + ".activityid in " + activityQuery();
    }
}
