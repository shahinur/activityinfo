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
import org.activityinfo.legacy.shared.command.result.Bucket;
import org.activityinfo.legacy.shared.impl.newpivot.source.SourceRow;
import org.activityinfo.legacy.shared.impl.pivot.PivotQueryContext;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author yuriyz on 6/27/14.
 */
public class CalculatedIndicatorsFunction  implements Function<List<SourceRow>, List<Bucket>> {

    private PivotQueryContext queryContext;

    public CalculatedIndicatorsFunction(PivotQueryContext queryContext) {
        this.queryContext = queryContext;
    }

    @Nullable
    @Override
    public List<Bucket> apply(List<SourceRow> sourceRows) {
        // TODO : analyze from expression which indicators to fetch !!!
        List<Bucket> list = Lists.newArrayList();
        return list;
    }
}