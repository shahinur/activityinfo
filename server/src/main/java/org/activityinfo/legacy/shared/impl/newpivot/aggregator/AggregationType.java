package org.activityinfo.legacy.shared.impl.newpivot.aggregator;
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

import org.activityinfo.legacy.shared.model.IndicatorDTO;

/**
 * @author yuriyz on 6/26/14.
 */
public enum AggregationType {
    SUM(IndicatorDTO.AGGREGATE_SUM),
    AVG(IndicatorDTO.AGGREGATE_AVG),
    SITE_COUNT(IndicatorDTO.AGGREGATE_SITE_COUNT);

    // references org.activityinfo.legacy.shared.model.IndicatorDTO.AGGREGATE_XXX
    private int value;

    AggregationType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AggregationType fromValue(int value) {
        for (AggregationType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
