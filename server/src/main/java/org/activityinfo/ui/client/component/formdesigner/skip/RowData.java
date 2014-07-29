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

import org.activityinfo.core.shared.expr.ExprFunction;
import org.activityinfo.model.form.FormField;

/**
 * @author yuriyz on 7/25/14.
 */
public class RowData {

    private FormField formField;
    private ExprFunction<Boolean, ?> joinFunction = RowDataBuilder.DEFAULT_JOIN_FUNCTION;
    private ExprFunction<Boolean, ?> function;
    private Object value;

    public RowData() {
    }

    public RowData(FormField formField, ExprFunction<Boolean, ?> joinFunction, ExprFunction<Boolean, ?> function, Object value) {
        this.formField = formField;
        this.joinFunction = joinFunction;
        this.function = function;
        this.value = value;
    }

    public FormField getFormField() {
        return formField;
    }

    public void setFormField(FormField formField) {
        this.formField = formField;
    }

    public ExprFunction<Boolean, ?> getJoinFunction() {
        return joinFunction;
    }

    public void setJoinFunction(ExprFunction<Boolean, ?> joinFunction) {
        this.joinFunction = joinFunction;
    }

    public ExprFunction<Boolean, ?> getFunction() {
        return function;
    }

    public void setFunction(ExprFunction<Boolean, ?> function) {
        this.function = function;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RowData rowData = (RowData) o;

        if (formField != null ? !formField.equals(rowData.formField) : rowData.formField != null) return false;
        if (function != null ? !function.equals(rowData.function) : rowData.function != null) return false;
        if (joinFunction != null ? !joinFunction.equals(rowData.joinFunction) : rowData.joinFunction != null)
            return false;
        if (value != null ? !value.equals(rowData.value) : rowData.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = formField != null ? formField.hashCode() : 0;
        result = 31 * result + (joinFunction != null ? joinFunction.hashCode() : 0);
        result = 31 * result + (function != null ? function.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
