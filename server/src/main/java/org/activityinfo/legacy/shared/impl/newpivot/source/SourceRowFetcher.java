package org.activityinfo.legacy.shared.impl.newpivot.source;
/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.bedatadriven.rebar.sql.client.*;
import com.bedatadriven.rebar.sql.client.query.SqlQuery;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.activityinfo.fp.client.Promise;
import org.activityinfo.legacy.shared.Log;
import org.activityinfo.legacy.shared.impl.Tables;
import org.activityinfo.legacy.shared.impl.newpivot.IndicatorAnalyzer;
import org.activityinfo.legacy.shared.impl.pivot.PivotQueryContext;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yuriyz on 6/27/14.
 */
public class SourceRowFetcher implements Function<IndicatorAnalyzer.Indicators, Promise<List<SourceRow>>> {

    private final PivotQueryContext queryContext;
    private final boolean pureIndicators;

    public SourceRowFetcher(PivotQueryContext queryContext, boolean pureIndicators) {
        this.queryContext = queryContext;
        this.pureIndicators = pureIndicators;
    }

    @Nonnull
    @Override
    public Promise<List<SourceRow>> apply(IndicatorAnalyzer.Indicators indicators) {
        Map<Integer,SqlResultSetRow> map = pureIndicators ? indicators.getPureIndicators() : indicators.getCalculatedIndicators();
        Set<Integer> indicatorIds = map.keySet();
        if (indicatorIds.isEmpty()) {
            throw new RuntimeException("Indicator id list is empty.");
        }

        SqlQuery query = new SqlQuery();
        query.from(Tables.INDICATOR_VALUE, "V");
        query.leftJoin(Tables.REPORTING_PERIOD, "Period").on("Period.ReportingPeriodId = V.ReportingPeriodId");
        query.leftJoin(Tables.SITE, "Site").on("Period.SiteId = Site.SiteId");
        query.leftJoin(Tables.INDICATOR, "Indicator").on("Indicator.IndicatorId = V.IndicatorId");
        query.leftJoin(Tables.ACTIVITY, "Activity").on("Indicator.ActivityId = Activity.ActivityId");
        query.leftJoin(Tables.USER_DATABASE, "UserDatabase").on("Activity.DatabaseId = UserDatabase.DatabaseId");
        query.where("Indicator.DateDeleted is NULL");
        query.where("Site.dateDeleted").isNull();
        query.where("Activity.dateDeleted").isNull();
        query.where("UserDatabase.dateDeleted").isNull();

        query.where("Indicator.IndicatorId").in(indicatorIds);

        query.appendColumn("V.Value");
        query.appendColumn("Indicator.IndicatorId");
        query.appendColumn("Indicator.Expression");
        query.appendColumn("Indicator.Name");
        query.appendColumn("Indicator.SortOrder");
        query.appendColumn("Indicator.Aggregation");
        query.appendColumn("Site.SiteId");
        query.appendColumn("Period.ReportingPeriodId");

        Log.debug("SourceRowFetcher SQL=" + query.sql());

        final Promise<List<SourceRow>> promise = new Promise<>();
        query.execute(queryContext.getExecutionContext().getTransaction(), new SqlResultCallback() {
            @Override
            public void onSuccess(SqlTransaction tx, SqlResultSet results) {
                promise.onSuccess(convert(results));
            }

            @Override
            public boolean onFailure(SqlException e) {
                promise.onFailure(e);
                return super.onFailure(e);
            }
        });

        return promise;
    }

    public static List<SourceRow> convert(SqlResultSet results) {
        final List<SourceRow> result = Lists.newArrayList();
        if (!results.getRows().isEmpty()) {
            for (SqlResultSetRow row : results.getRows()) {
                result.add(convert(row));
            }
        }
        return result;
    }

    public static SourceRow convert(SqlResultSetRow row) {
        final SourceRow result = new SourceRow();
        result.setValue(row.getDouble("Value"));
        result.setIndicatorName(row.getString("Name"));
        result.setIndicatorId(row.getInt("IndicatorId"));
        result.setIndicatorSortOrder(row.getInt("SortOrder"));
        result.setSiteId(row.getInt("SiteId"));
        result.setReportingPeriodId(row.getInt("ReportingPeriodId"));
        result.setExpression(row.getString("Expression"));
        result.setAggregation(row.getInt("Aggregation"));
        return result;
    }
}
