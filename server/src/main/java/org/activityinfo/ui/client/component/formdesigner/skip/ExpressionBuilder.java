package org.activityinfo.ui.client.component.formdesigner.skip;
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

import java.util.List;

/**
 * @author yuriyz on 7/25/14.
 */
public class ExpressionBuilder {

    private List<RowData> rows;
    private String expression = "";

    public ExpressionBuilder(List<RowData> rows) {
        this.rows = rows;
    }

    public String build() {
        for (int i = 0; i < rows.size(); i++) {
            handleRow(rows.get(i), i);
        }
        return expression;
    }

    private void handleRow(RowData row, int index) {
        if (index != 0) {
            expression += row.getJoinFunction().getId();
        }

//        String selectedValue = row.getJoinFunction().getValue(row.getJoinFunction().getSelectedIndex());
    }
}
