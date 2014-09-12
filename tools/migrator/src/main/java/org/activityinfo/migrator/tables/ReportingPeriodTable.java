package org.activityinfo.migrator.tables;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activityinfo.migrator.ResourceMigrator;
import org.activityinfo.migrator.ResourceWriter;
import org.activityinfo.migrator.filter.MigrationContext;
import org.activityinfo.migrator.filter.MigrationFilter;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.ReferenceValue;
import org.activityinfo.model.type.number.Quantity;
import org.activityinfo.model.type.primitive.TextValue;
import org.activityinfo.model.type.time.LocalDate;
import org.activityinfo.model.type.time.MonthValue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import static org.activityinfo.model.legacy.CuidAdapter.*;

public class ReportingPeriodTable extends ResourceMigrator {

    private final MigrationFilter filter;
    private final MigrationContext context;

    public ReportingPeriodTable(MigrationContext context) {
        this.context = context;
        this.filter = context.filter();
    }

    @Override
    public void getResources(Connection connection, ResourceWriter writer) throws Exception {

        List<Integer> activities = Lists.newArrayList();

        try(Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select activityid from activity A WHERE " +
                " dateDeleted is null and reportingFrequency = 1 AND " +
                    context.filter().activityFilter("A"))) {

            while(rs.next()) {
                activities.add(rs.getInt(1));
            }
        }

        for(Integer activityId : activities) {
            writeActivity(connection, writer, activityId);
        }
    }

    private void writeActivity(Connection connection, ResourceWriter writer, int activityId) throws Exception {
        String sql = "SELECT S.SiteID, " +
                        "S.ActivityID, " +
                        "RP.ReportingPeriodId," +
                        "RP.Date1, " +
                        "RP.Date2 " +
                     "FROM site S " +
                     "LEFT JOIN reportingperiod RP ON (S.siteId=RP.siteId) " +
                     "WHERE S.dateDeleted is null and " +
                        " RP.dateDeleted is null and " +
                        " S.activityId = " + activityId;


        Map<Integer, FormInstance> periods = Maps.newHashMap();


        try(Statement statement = connection.createStatement()) {
            try(ResultSet rs = statement.executeQuery(sql)) {

                while(rs.next()) {

                    int siteId = rs.getInt("siteId");
                    int reportingPeriodId = rs.getInt("reportingPeriodId");
                    ResourceId instanceId = context.resourceId(MONTHLY_REPORT_INSTANCE_DOMAIN, reportingPeriodId);
                    ResourceId siteResourceId = context.resourceId(SITE_DOMAIN, siteId);
                    ResourceId classId = context.resourceId(MONTHLY_REPORT_CLASS_DOMAIN, rs.getInt("activityId"));

                    LocalDate date = new LocalDate(rs.getDate("Date2"));

                    FormInstance resource = new FormInstance(instanceId, classId);
                    resource.set(field(classId, SITE_FIELD), new ReferenceValue(siteResourceId));
                    resource.set(field(classId, DATE_FIELD), new MonthValue(date.getYear(), date.getMonthOfYear()));
                    periods.put(reportingPeriodId, resource);
                }
            }
        }

        populateIndicators(connection, periods, activityId);

        for(FormInstance site : periods.values()) {
            writer.writeResource(site.asResource(), null, null);
        }
    }

    private void populateIndicators(Connection connection, Map<Integer, FormInstance> periods, Integer activityId) throws SQLException {

        String sql = "SELECT V.*, I.Type, I.Units, RP.SiteId " +
                     "FROM indicatorvalue V " +
                     "LEFT JOIN reportingperiod RP ON (V.ReportingPeriodId=RP.ReportingPeriodId) " +
                     "LEFT JOIN indicator I on (V.IndicatorId=I.IndicatorId) " +
                     "LEFT JOIN site S on (RP.Siteid=S.siteid) " +
                     "WHERE S.activityId = " + activityId;

        try(Statement statement = connection.createStatement()) {
            try(ResultSet rs = statement.executeQuery(sql)) {

                while(rs.next()) {

                    int siteId = rs.getInt("siteId");
                    int periodId = rs.getInt("reportingPeriodId");
                    FormInstance period = periods.get(periodId);
                    if(period != null) {
                        int indicatorId = rs.getInt("indicatorId");
                        ResourceId fieldId = indicatorField(indicatorId);
                        String type = rs.getString("Type");

                        switch (type) {
                            case "QUANTITY":
                                double quantity = rs.getDouble("Value");
                                if(!rs.wasNull()) {
                                    period.set(fieldId, new Quantity(quantity, rs.getString("units")));
                                }
                                break;
                            case "NARRATIVE":
                            case "FREE_TEXT":
                                period.set(fieldId, TextValue.valueOf(rs.getString("TextValue")));
                                break;
                        }
                    }
                }
            }
        }
    }

}
