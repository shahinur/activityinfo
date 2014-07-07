package org.activityinfo.ui.client.component.formdesigner;
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

/**
 * @author yuriyz on 07/07/2014.
 */
public enum ControlType {
    SINGLE_LINE_TEXT(0),
    MULTI_LINE_TEXT(0),
    MULTIPLE_CHOICE(1),
    DROP_DOWN(1),
    CHECKBOX(1),
    NUMBER(1),
    SECTION_BREAK(1),
    PAGE_BREAK(1);

    private final int column;

    ControlType(int column) {
        this.column = column;
        if (column != 0 && column != 1) {
            throw new IllegalArgumentException("Unsupported column. Only two columns are supported right now.");
        }
    }

    public int getColumn() {
        return column;
    }

    public String getLabel() {
        switch (this) {
            case SINGLE_LINE_TEXT:
                return "Single line text";
            case MULTI_LINE_TEXT:
                return "Multiline text";
            case CHECKBOX:
                return "Check box";
            case DROP_DOWN:
                return "Drop down";
            case MULTIPLE_CHOICE:
                return "Multiple choice";
            case NUMBER:
                return "Number";
            case PAGE_BREAK:
                return "Page break";
            case SECTION_BREAK:
                return "Section break";
        }
        return "Unknown";
    }
}
