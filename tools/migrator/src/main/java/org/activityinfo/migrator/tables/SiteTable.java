package org.activityinfo.migrator.tables;

import com.google.api.client.util.Lists;
import com.google.api.client.util.Sets;
import com.google.common.base.Strings;
import com.google.api.client.util.Maps;
import org.activityinfo.model.resource.Reference;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.shared.CuidAdapter;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.migrator.ResourceMigrator;
import org.activityinfo.migrator.resource.DateRange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Set;

import static org.activityinfo.model.shared.CuidAdapter.*;

public class SiteTable extends ResourceMigrator {

    @Override
    public Iterable<Resource> getResources(Connection connection) throws SQLException {

        String sql = "SELECT S.*, " +
                        "LT.boundAdminLevelId, " +
                        "A.ReportingFrequency, " +
                        "A.DatabaseId  " +
                     "FROM site S " +
                     "LEFT JOIN activity A ON (A.activityId=S.activityId) " +
                     "LEFT JOIN locationtype LT ON (A.locationTypeId=LT.locationtypeid)";

        Map<Integer, Resource> sites = Maps.newHashMap();

        try(Statement statement = connection.createStatement()) {
            try(ResultSet rs = statement.executeQuery(sql)) {

                while(rs.next()) {

                    int siteId = rs.getInt("siteId");

                    Resource resource = Resources.createResource()
                    .setId(resourceId(CuidAdapter.SITE_DOMAIN, siteId))
                    .setOwnerId(resourceId(ACTIVITY_DOMAIN, rs.getInt("activityId")))
                    .set(CLASS_FIELD, resourceId(ACTIVITY_DOMAIN, rs.getInt("activityId")))
                    .set(PARTNER_FIELD, partnerInstanceId(rs.getInt("databaseId"), rs.getInt("partnerId")));

                    int reportingFrequency = rs.getInt("ReportingFrequency");
                    if(reportingFrequency == ActivityTable.ONCE) {
                        resource.set(START_DATE_FIELD, DateRange.startDate(rs));
                        resource.set(END_DATE_FIELD, DateRange.endDate(rs));
                    }

                    if(!isAdminBound(rs)) {
                        resource.set(LOCATION_FIELD, resourceId(LOCATION_DOMAIN, rs.getInt("LocationId")));
                    }
                    sites.put(siteId, resource);
                }
            }
        }

        populateAttributes(connection, sites);

        populateIndicators(connection, sites);
        populateBoundAdminLevels(connection, sites);

        return sites.values();
    }

    private void populateAttributes(Connection connection, Map<Integer, Resource> sites) throws SQLException {

        String sql =
                "SELECT V.siteId, V.attributeid, V.Value, A.attributeGroupId " +
                "FROM attributevalue V " +
                "INNER JOIN attribute A ON (A.attributeId=V.attributeID) " +
                "WHERE V.value IS NOT NULL " +
                "ORDER BY V.siteId, A.AttributeGroupId";

        int currentSiteId = -1;
        int currentGroupId = -1;
        Set<Reference> currentValue = Sets.newHashSet();

        try(Statement statement = connection.createStatement()) {
            try(ResultSet rs = statement.executeQuery(sql)) {

                while(rs.next()) {

                    int siteId = rs.getInt("siteId");
                    int groupId = rs.getInt("attributeGroupId");

                    if(currentSiteId != siteId || currentGroupId != groupId) {
                        if(!currentValue.isEmpty()) {
                            Resource site = sites.get(currentSiteId);
                            site.set(CuidAdapter.attributeGroupName(currentGroupId),
                                    Lists.newArrayList(currentValue));
                        }
                        currentSiteId = siteId;
                        currentGroupId = groupId;
                        currentValue = Sets.newHashSet();
                    }

                    int attributeId = rs.getInt("attributeId");
                    currentValue.add(Reference.to(resourceId(ATTRIBUTE_DOMAIN, attributeId)));
                }

                if(!currentValue.isEmpty()) {
                    Resource site = sites.get(currentSiteId);
                    site.set(CuidAdapter.attributeGroupName(currentGroupId),
                            Lists.newArrayList(currentValue));
                }
            }
        }
    }

    private void populateIndicators(Connection connection, Map<Integer, Resource> sites) throws SQLException {

        String sql = "SELECT V.*, I.Type, RP.SiteId " +
                     "FROM indicatorvalue V " +
                     "LEFT JOIN reportingperiod RP ON (V.ReportingPeriodId=RP.ReportingPeriodId) " +
                     "LEFT JOIN site S ON (RP.SiteId = S.SiteId) " +
                     "LEFT JOIN indicator I ON (V.IndicatorId = I.IndicatorId) " +
                     "LEFT JOIN activity A ON (S.ActivityId = A.ActivityId) " +
                     "WHERE A.ReportingFrequency = 0";

        try(Statement statement = connection.createStatement()) {
            try(ResultSet rs = statement.executeQuery(sql)) {

                while(rs.next()) {

                    int siteId = rs.getInt("siteId");
                    Resource site = sites.get(siteId);
                    if(site != null) {

                        int indicatorId = rs.getInt("indicatorId");
                        String fieldId = "I" + indicatorId;
                        String type = rs.getString("Type");

                        switch (type) {
                            case "QUANTITY":
                                double quantity = rs.getDouble("Value");
                                if(!rs.wasNull()) {
                                    site.set(fieldId, quantity);
                                }
                                break;
                            case "FREE_TEXT":
                                String textValue = Strings.emptyToNull(rs.getString("TextValue"));
                                site.set(fieldId, textValue);
                                break;
                        }
                    }
                }
            }
        }
    }


    private void populateBoundAdminLevels(Connection connection, Map<Integer, Resource> sites) throws SQLException {

        String sql = "SELECT S.SiteId, E.AdminEntityId " +
                     "FROM site S " +
                     "LEFT JOIN location L ON (S.LocationId = L.LocationID) " +
                     "LEFT JOIN locationtype LT ON (L.LocationTypeID = LT.LocationTypeId) " +
                     "LEFT JOIN locationadminlink LK ON (L.LocationId=LK.LocationId) " +
                     "LEFT JOIN adminentity E ON (LK.AdminEntityId=E.AdminEntityId) " +
                     "WHERE E.AdminLevelId = LT.BoundAdminLevelId";

        try(Statement statement = connection.createStatement()) {
            try(ResultSet rs = statement.executeQuery(sql)) {

                while(rs.next()) {

                    int siteId = rs.getInt("siteId");
                    Resource resource = sites.get(siteId);
                    if(resource != null) {
                        ResourceId entityId = CuidAdapter.resourceId(ADMIN_ENTITY_DOMAIN, rs.getInt("adminEntityId"));
                        assert entityId != null;
                        resource.set("location", entityId);
                    }
                }
            }
        }
    }

    private boolean isAdminBound(ResultSet rs) throws SQLException {
        rs.getInt("boundAdminLevelId");
        return !rs.wasNull();
    }
}
