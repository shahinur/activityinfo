package org.activityinfo.legacy.shared.impl.newpivot;
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
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.activityinfo.fp.client.Promise;
import org.activityinfo.legacy.shared.command.DimensionType;
import org.activityinfo.legacy.shared.impl.Tables;
import org.activityinfo.legacy.shared.impl.newpivot.aggregator.AggregationType;
import org.activityinfo.legacy.shared.impl.pivot.PivotQueryContext;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author yuriyz on 6/27/14.
 */
public class IndicatorAnalyzer implements Function<Void, Promise<IndicatorAnalyzer.Indicators>> {

    public static class Indicators {
        private final Map<Integer, SqlResultSetRow> pureIndicators = Maps.newHashMap();
        private final Map<Integer, SqlResultSetRow> calculatedIndicators = Maps.newHashMap();

        public Map<Integer, SqlResultSetRow> getPureIndicators() {
            return pureIndicators;
        }

        public Map<Integer, SqlResultSetRow> getCalculatedIndicators() {
            return calculatedIndicators;
        }
    }

    private final PivotQueryContext queryContext;

    public IndicatorAnalyzer(PivotQueryContext queryContext) {
        this.queryContext = queryContext;
    }

    @Nonnull
    @Override
    public Promise<Indicators> apply(Void input) {
        final Promise<Indicators> promise = new Promise<>();
        final Indicators result = new Indicators();

        SqlQuery query = new SqlQuery();
        query.from(Tables.INDICATOR);
        query.where("IndicatorId").in(queryContext.getCommand().getFilter().getRestrictions(DimensionType.Indicator));
        query.appendColumn("IndicatorId");
        query.appendColumn("Aggregation");
        query.appendColumn("Expression");
        query.execute(queryContext.getExecutionContext().getTransaction(), new SqlResultCallback() {
            @Override
            public boolean onFailure(SqlException e) {
                promise.onFailure(e);
                return super.onFailure(e);
            }

            @Override
            public void onSuccess(SqlTransaction tx, SqlResultSet results) {
                for (SqlResultSetRow row : results.getRows()) {
                    int id = row.getInt("IndicatorId");
                    String expression = row.getString("Expression");
                    AggregationType aggregationType = AggregationType.fromValue(row.getInt("Aggregation"));
                    if (aggregationType == AggregationType.SITE_COUNT) {
                        continue; // let SQL pivot handler handle it
                    }
                    if (Strings.isNullOrEmpty(expression)) {
                        result.getPureIndicators().put(id, row);
                    } else {
                        result.getCalculatedIndicators().put(id, row);
                    }
                }
                promise.onSuccess(result);
            }
        });

        return promise;
    }

}
