package org.activityinfo.service.cubes;

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

import com.google.common.collect.Ordering;
import org.activityinfo.model.table.Bucket;

import java.util.*;

public class PivotTableDataBuilder {

    private String emptyDimensionValue = "(Empty)";

    public PivotTableData build(List<String> rowDims,
                                List<String> colDims,
                                List<Bucket> buckets) {

        PivotTableData table = new PivotTableData();
        Map<String, Comparator<PivotTableData.Axis>> comparators = new HashMap<>();
        for (Bucket bucket : buckets) {

            PivotTableData.Axis column = colDims.isEmpty() ? table.getRootColumn() : find(table.getRootColumn(),
                    colDims.iterator(),
                    comparators,
                    bucket);
            PivotTableData.Axis row = rowDims.isEmpty() ? table.getRootRow() : find(table.getRootRow(),
                    rowDims.iterator(),
                    comparators,
                    bucket);

            row.setValue(column, bucket.getValue());
        }
        return table;
    }

//    protected Map<String, Comparator<PivotTableData.Axis>> createComparators(Set<Dimension> dimensions) {
//        Map<Dimension, Comparator<PivotTableData.Axis>> map = new HashMap<Dimension, Comparator<PivotTableData.Axis>>();
//
//        for (Dimension dimension : dimensions) {
//            if (dimension.isOrderDefined()) {
//                map.put(dimension, new DefinedCategoryComparator(dimension.getOrdering()));
//            } else {
//                map.put(dimension, new CategoryComparator());
//            }
//        }
//        return map;
//    }

    protected PivotTableData.Axis find(PivotTableData.Axis axis,
                                       Iterator<String> dimensionIterator,
                                       Map<String, Comparator<PivotTableData.Axis>> comparators,
                                       Bucket result) {

        String childDimension = dimensionIterator.next();
        String category = result.getDimensionValue(childDimension);
        PivotTableData.Axis child = null;

        if(category == null) {
            child = axis;
        } else {
            child = axis.getChild(category);
            if (child == null) {

                String categoryLabel;
                if (category == null) {
                    categoryLabel = emptyDimensionValue;
                } else {
                    categoryLabel = category;
//                if (categoryLabel == null) {
//                    categoryLabel = category.getLabel();
//                }
                }

                child = axis.addChild(childDimension,
                    result.getDimensionValue(childDimension),
                    categoryLabel,
                    Ordering.<String>natural());

            }
        }
        if (dimensionIterator.hasNext()) {
            return find(child, dimensionIterator, comparators, result);
        } else {
            return child;
        }
    }
}
