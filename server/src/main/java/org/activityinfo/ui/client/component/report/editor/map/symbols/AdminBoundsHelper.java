package org.activityinfo.ui.client.component.report.editor.map.symbols;

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

import org.activityinfo.legacy.shared.model.ActivityDTO;
import org.activityinfo.legacy.shared.model.AdminEntityDTO;
import org.activityinfo.legacy.shared.model.AdminLevelDTO;
import org.activityinfo.legacy.shared.model.HasAdminEntityValues;
import org.activityinfo.model.type.geo.GeoExtents;

import java.util.Collection;

/**
 * Utility class to help calculate the lat/lng bounds of an activity site from
 * the admin entity membership
 *
 * @author Alex Bertram
 */
public final class AdminBoundsHelper {

    private AdminBoundsHelper() {
    }

    /**
     * Calculates the normative lat/lng bounds for a given site as function of
     * AdminEntity entity membership.
     * <p/>
     * For example, if a site is marked as being within Country A, the Province
     * B and Health Zone C, the method will return the intersection of the
     * bounds for A, B, and C, which are provided by
     * {@link org.activityinfo.legacy.shared.model.AdminEntityDTO#getBounds()} and
     * {@link org.activityinfo.legacy.shared.model.CountryDTO#getBounds()}
     *
     * @param activity
     * @param entityAccessor an adapter class that provides AdminEntity membership for some
     *                       representation of a site.
     * @return the normative lat/lng bounds
     */
    public static GeoExtents calculate(ActivityDTO activity, HasAdminEntityValues entityAccessor) {
        return calculate(activity.getBounds(), activity.getAdminLevels(), entityAccessor);
    }

    /**
     * Calculates the normative lat/lng bounds for a given site as function of
     * AdminEntity entity membership.
     * <p/>
     * For example, if a site is marked as being within Country A, the Province
     * B and Health Zone C, the method will return the intersection of the
     * bounds for A, B, and C, which are provided by
     * {@link org.activityinfo.legacy.shared.model.AdminEntityDTO#getBounds()} and
     * {@link org.activityinfo.legacy.shared.model.CountryDTO#getBounds()}
     *
     *
     * @param formClassBounds
     * @param entityAccessor an adapter class that provides AdminEntity membership for some
     *                       representation of a site.
     * @return the normative lat/lng bounds
     */
    public static GeoExtents calculate(GeoExtents formClassBounds, Collection<AdminLevelDTO> levels,
                                    HasAdminEntityValues entityAccessor) {
        GeoExtents bounds = null;
        if (formClassBounds != null) {
            bounds = new GeoExtents(formClassBounds);
        }
        if (bounds == null) {
            bounds = GeoExtents.maxGeoBounds();
        }

        for (AdminLevelDTO level : levels) {
            AdminEntityDTO entity = entityAccessor.getAdminEntity(level.getId());
            if (entity != null && entity.hasBounds()) {
                bounds = bounds.intersect(entity.getBounds());
            }
        }

        return bounds;
    }

    /**
     * @param bounds
     * @param levels
     * @param getter
     * @return
     */
    public static String name(GeoExtents bounds, Collection<AdminLevelDTO> levels, HasAdminEntityValues getter) {
        // find the entities that are the limiting bounds.
        // E.g., if the user selects North Kivu, distict de North Kivu, and
        // territoire
        // de Beni, the name we give to this bounds should just be 'Beni'.

        if (bounds == null) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder();
            for (AdminLevelDTO level : levels) {
                AdminEntityDTO entity = getter.getAdminEntity(level.getId());
                if (entity != null && entity.hasBounds()) {
                    GeoExtents b = entity.getBounds();

                    if (b != null && (!b.contains(bounds) || b.equals(bounds))) {
                        if (sb.length() != 0) {
                            sb.append(", ");
                        }
                        sb.append(entity.getName());

                    }
                }
            }
            return sb.toString();
        }
    }
}
