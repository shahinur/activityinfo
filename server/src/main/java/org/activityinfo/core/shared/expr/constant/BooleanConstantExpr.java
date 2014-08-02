package org.activityinfo.core.shared.expr.constant;
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

import org.activityinfo.core.shared.expr.ExprNode;
import org.activityinfo.model.type.primitive.BooleanFieldValue;

/**
 * @author yuriyz on 7/23/14.
 */
public class BooleanConstantExpr extends ExprNode<Boolean> implements IsConstantExpr  {

    private boolean value;

    public BooleanConstantExpr(boolean value) {
        super();
        this.value = value;
    }

    public BooleanConstantExpr(BooleanFieldValue value) {
        this(value.asBoolean());
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public Boolean evalReal() {
        return value;
    }

    @Override
    public String toString() {
        return asExpression();
    }

    @Override
    public String asExpression() {
        return Boolean.toString(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BooleanConstantExpr that = (BooleanConstantExpr) o;

        if (value != that.value) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (value ? 1 : 0);
    }
}