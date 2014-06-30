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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activityinfo.legacy.shared.command.DimensionType;
import org.activityinfo.legacy.shared.command.result.Bucket;
import org.activityinfo.legacy.shared.impl.newpivot.aggregator.AggregationType;
import org.activityinfo.legacy.shared.impl.newpivot.aggregator.Aggregator;
import org.activityinfo.legacy.shared.impl.newpivot.aggregator.AggregatorFactory;
import org.activityinfo.legacy.shared.impl.newpivot.source.SourceRow;
import org.activityinfo.legacy.shared.impl.pivot.PivotQueryContext;
import org.activityinfo.legacy.shared.reports.content.EntityCategory;
import org.activityinfo.legacy.shared.reports.content.TargetCategory;
import org.activityinfo.legacy.shared.reports.model.Dimension;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * @author yuriyz on 6/27/14.
 */
public class PureIndicatorsFunction implements Function<List<SourceRow>, List<Bucket>> {

    private PivotQueryContext queryContext;

    public PureIndicatorsFunction(PivotQueryContext queryContext) {
        this.queryContext = queryContext;
    }

    @Nullable
    @Override
    public List<Bucket> apply(List<SourceRow> input) {
        final Map<SourceRow, Aggregator> indicatorIdToAggregator = Maps.newHashMap();
        for (SourceRow row : input) {
            AggregationType type = AggregationType.fromValue(row.getAggregation());

            if (!indicatorIdToAggregator.containsKey(row)) {
                indicatorIdToAggregator.put(row, AggregatorFactory.create(type));
            }

            if (type == AggregationType.SUM || type == AggregationType.AVG) {
                indicatorIdToAggregator.get(row).aggregate(row.getValue());
            } else if (type == AggregationType.SITE_COUNT) {
                indicatorIdToAggregator.get(row).aggregate(row.getSiteId());
            }
        }

        for (Map.Entry<SourceRow, Aggregator> entry : indicatorIdToAggregator.entrySet()) {
            final SourceRow row = entry.getKey();
            Aggregator aggregator = entry.getValue();

            final Bucket bucket = new Bucket();
            bucket.setAggregationMethod(row.getAggregation());
            bucket.setCount(aggregator.count());
            bucket.setSum(aggregator.value());
            bucket.setCategory(new Dimension(DimensionType.Target), TargetCategory.REALIZED);

            // todo it's not clear for me this category<->dimension technic -> clarify ?
            for (Dimension dimension : queryContext.getCommand().getDimensions()) {
                bucket.setCategory(dimension, new EntityCategory(row.getIndicatorId(), row.getIndicatorName(), row.getIndicatorSortOrder()));
            }
            queryContext.addBucket(bucket);
        }

        return Lists.newArrayList();
    }
}
