package org.activityinfo.core.shared.importing.strategy;
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

import com.google.common.collect.Lists;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.model.formTree.FieldPath;

import java.util.List;
import java.util.Map;

/**
 * @author yuriyz on 5/20/14.
 */
public class InstanceScoreSourceBuilder {

    private final Map<FieldPath, Integer> referenceFields;
    private final List<ColumnAccessor> sourceColumns;

    public InstanceScoreSourceBuilder(Map<FieldPath, Integer> referenceFields, List<ColumnAccessor> sourceColumns) {
        this.referenceFields = referenceFields;
        this.sourceColumns = sourceColumns;
    }

    public InstanceScoreSource build(List<Projection> projections) {
        final List<ResourceId> instanceResourceIdList = Lists.newArrayList();
        final List<String[]> referenceValueList = Lists.newArrayList();

        for (Projection projection : projections) {
            instanceResourceIdList.add(projection.getRootInstanceId());
            referenceValueList.add(SingleClassImporter.toArray(projection, referenceFields, sourceColumns.size()));
        }
        return new InstanceScoreSource(sourceColumns, instanceResourceIdList, referenceValueList);
    }
}
