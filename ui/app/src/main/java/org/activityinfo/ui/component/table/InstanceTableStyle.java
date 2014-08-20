package org.activityinfo.ui.component.table;
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

import org.activityinfo.ui.style.BaseStyles;

/**
 * @author yuriyz on 3/21/14.
 */
public class InstanceTableStyle {

    public static final InstanceTableStyle INSTANCE = new InstanceTableStyle();

    public String toolbar() { return BaseStyles.INSTANCE_TABLE_TOOBAR.getClassNames(); }

    public String columnHalfWidth() { return BaseStyles.COLUMN_HALF_WIDTH.getClassNames(); }

    public String header() { return BaseStyles.TABLE_HEADER.getClassNames(); }

    public String headerHover() { return BaseStyles.HEADER_HOVER.getClassNames(); }

    public String rowPresent() { return BaseStyles.ROW_PRESENT.getClassNames(); }
}

