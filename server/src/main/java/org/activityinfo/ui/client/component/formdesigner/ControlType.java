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

    SINGLE_LINE_TEXT(0, Metrics.DEFAULT_STYLE_NAME),
    MULTI_LINE_TEXT(0, Metrics.DEFAULT_STYLE_NAME),
    MULTIPLE_CHOICE(1, Metrics.DEFAULT_STYLE_NAME),
    DROP_DOWN(1, Metrics.DEFAULT_STYLE_NAME),
    CHECKBOX(1, Metrics.DEFAULT_STYLE_NAME),
    NUMBER(1, Metrics.DEFAULT_STYLE_NAME),
    SECTION_BREAK(1, Metrics.DEFAULT_STYLE_NAME),
    PAGE_BREAK(1, Metrics.DEFAULT_STYLE_NAME);

    private final int column;
    private final String styleName;

    ControlType(int column, String styleName) {
        if (column != 0 && column != 1) {
            throw new IllegalArgumentException("Unsupported column. Only two columns are supported right now.");
        }
        this.column = column;
        this.styleName = styleName;
    }

    public String getStyleName() {
        return styleName;
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
