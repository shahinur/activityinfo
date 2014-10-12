package org.activityinfo.legacy.shared.command.result;

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

import org.activityinfo.legacy.shared.model.SiteDTO;
import org.activityinfo.legacy.shared.model.SitePointDTO;
import org.activityinfo.legacy.shared.reports.util.mapping.Extents;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alex Bertram
 */
public class SitePointList implements CommandResult {

    private Extents bounds;
    private List<SitePointDTO> points;

    private SitePointList() {

    }

    public SitePointList(Extents bounds, List<SitePointDTO> points) {
        this.bounds = bounds;
        this.points = points;
    }

    public Extents getBounds() {
        return bounds;
    }

    public void setBounds(Extents bounds) {
        this.bounds = bounds;
    }

    public List<SitePointDTO> getPoints() {
        return points;
    }

    public void setPoints(List<SitePointDTO> points) {
        this.points = points;
    }

}
