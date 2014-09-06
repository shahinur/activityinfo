package org.activityinfo.migrator.filter;

public class NullFilter implements MigrationFilter {

    private static final String NULL_CONDITION = "(1=1)";

    @Override
    public String adminLevelFilter(String tableAlias) {
        return NULL_CONDITION;
    }

    @Override
    public String activityFilter(String activityTableAlias) {
        return NULL_CONDITION;
    }

    @Override
    public String adminEntityFilter() {
        return NULL_CONDITION;
    }

    @Override
    public String countryFilter() {
        return NULL_CONDITION;
    }

    @Override
    public String locationFilter() {
        return NULL_CONDITION;
    }

    @Override
    public String locationTypeFilter(String locationTypeTableAlias) {
        return NULL_CONDITION;
    }

    @Override
    public String databaseFilter() {
        return NULL_CONDITION;
    }

    @Override
    public String partnerFilter(String partnerTableAlias) {
        return NULL_CONDITION;
    }

    @Override
    public String projectFilter() {
        return NULL_CONDITION;
    }

    @Override
    public String siteFilter(String siteTableAlias) {
        return NULL_CONDITION;
    }

    @Override
    public String indicatorFilter(String indicatorTableAlias) {
        return NULL_CONDITION;
    }

    @Override
    public String attributeGroupFilter(String attributeGroupInActivityTableAlias) {
        return NULL_CONDITION;
    }

    @Override
    public String attributeFilter(String attributeGroupTableAlias) {
        return NULL_CONDITION;
    }
}
