package org.activityinfo.ui.client.page.entry.column;
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

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import org.activityinfo.ui.client.util.GwtUtil;

/**
 * @author yuriyz on 6/20/14.
 */
public class StringWithTooltipRenderer implements GridCellRenderer {
    @Override
    public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex, ListStore store, Grid grid) {
        Object value = model.get(property);
        if (value instanceof String) {
            return GwtUtil.valueWithTooltip((String) value);
        } else if (value != null) {
            return GwtUtil.valueWithTooltip(value.toString());
        }
        return "";
    }
}