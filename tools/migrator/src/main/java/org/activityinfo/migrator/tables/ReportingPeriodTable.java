package org.activityinfo.migrator.tables;

import com.google.common.base.Strings;
import com.google.api.client.util.Maps;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.migrator.ResourceMigrator;
import org.activityinfo.migrator.resource.DateRange;
import org.activityinfo.model.resource.Resource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.logging.Logger;

import static org.activityinfo.model.shared.CuidAdapter.*;

public class ReportingPeriodTable extends ResourceMigrator {

    private static final Logger LOGGER = Logger.getLogger(ReportingPeriodTable.class.getName());

    @Override
    public Iterable<Resource> getResources(Connection connection) throws SQLException {

        String sql = "SELECT RP.* " +
                     "FROM reportingperiod RP " +
                     "LEFT JOIN site S ON (S.SiteId=RP.SiteId) " +
                     "LEFT JOIN activity A ON (A.activityId=S.activityId) " +
                     "WHERE A.ReportingFrequency=1";

        Map<Integer, Resource> resources = Maps.newHashMap();

        try(Statement statement = connection.createStatement()) {
            try(ResultSet rs = statement.executeQuery(sql)) {

                while(rs.next()) {

                    int periodId = rs.getInt("reportingPeriodId");

                    Resource resource = Resources.createResource()
                    .setId(resourceId(MONTHLY_REPORT_INSTANCE, periodId))
                    .setOwnerId(resourceId(SITE_DOMAIN, rs.getInt("siteId")))
                    .set(START_DATE_FIELD, DateRange.startDate(rs))
                    .set(END_DATE_FIELD, DateRange.endDate(rs));

                    resources.put(periodId, resource);
                }
            }
        }

        populateIndicators(connection, resources);

        return resources.values();
    }

    private void populateIndicators(Connection connection, Map<Integer, Resource> resources) throws SQLException {


        String sql = "SELECT V.*, " +
                        "I.Type " +
                     "FROM indicatorvalue V " +
                     "LEFT JOIN reportingperiod RP on (V.ReportingPeriodId=RP.ReportingPeriodId) " +
                     "LEFT JOIN indicator I on (V.IndicatorId = I.IndicatorId) " +
                     "LEFT JOIN activity A on (I.ActivityId = A.ActivityId) " +
                     "WHERE A.ReportingFrequency = 1";

        try(Statement statement = connection.createStatement()) {
            try(ResultSet rs = statement.executeQuery(sql)) {

                while(rs.next()) {
                    int periodId = rs.getInt("reportingPeriodId");
                    Resource resource = resources.get(periodId);
                    assert resource != null : "Reporting Period " + periodId;

                    int indicatorId = rs.getInt("indicatorId");
                    String fieldId = "I" + indicatorId;
                    String type = rs.getString("Type");

                    switch (type) {
                        case "QUANTITY":
                            double quantity = rs.getDouble("Value");
                            if(!rs.wasNull()) {
                                resource.set(fieldId, quantity);
                            }
                            break;
                        case "FREE_TEXT":
                            String textValue = Strings.emptyToNull(rs.getString("TextValue"));
                            resource.set(fieldId, textValue);
                            break;
                    }
                }
            }
        }
    }
}
