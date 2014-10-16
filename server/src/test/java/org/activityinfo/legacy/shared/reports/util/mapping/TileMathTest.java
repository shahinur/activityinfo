package org.activityinfo.legacy.shared.reports.util.mapping;

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

import org.activityinfo.legacy.shared.reports.content.Point;
import org.activityinfo.model.type.geo.GeoPoint;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class TileMathTest {

    @Test
    public void inverse() {
        GeoPoint latlng = new GeoPoint(15, 30);
        Point px = TileMath.fromLatLngToPixel(latlng, 6);

        GeoPoint inverse = TileMath.inverse(px, 6);

        assertThat("longitude", inverse.getLongitude(), equalTo(latlng.getLongitude()));
        assertThat("latitude", inverse.getLatitude(),
                closeTo(latlng.getLatitude(), 0.0001));
    }

    private Matcher<Double> closeTo(final double x, final double epsilon) {
        return new TypeSafeMatcher<Double>() {

            @Override
            public void describeTo(Description d) {
                d.appendText("within ").appendValue(d)
                        .appendText(" of ").appendValue(x);
            }

            @Override
            public boolean matchesSafely(Double item) {
                return Math.abs(item - x) < epsilon;
            }

        };
    }

}
