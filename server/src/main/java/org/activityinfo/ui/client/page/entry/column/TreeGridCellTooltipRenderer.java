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
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import org.activityinfo.ui.client.util.GwtUtil;

/**
 * @author yuriyz on 6/23/14.
 */
public class TreeGridCellTooltipRenderer extends TreeGridCellRenderer {
    public TreeGridCellTooltipRenderer() {
    }

    @Override
    protected String getText(TreeGrid grid, ModelData model, String property, int rowIndex, int colIndex) {
        String text = super.getText(grid, model, property, rowIndex, colIndex);
        return GwtUtil.valueWithTooltip(text);
    }
}
