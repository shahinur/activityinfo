package org.activityinfo.core.shared.expr;
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
 * @author yuriyz on 6/2/14.
 */
public class PlaceholderExpr extends ExprNode {

    private final String placeholder;
    private final PlaceholderExprResolver resolver;

    private Double value = null;

    public PlaceholderExpr(String placeholder) {
        this(placeholder, null);
    }

    public PlaceholderExpr(String placeholder, PlaceholderExprResolver resolver) {
        this.placeholder = placeholder;
        this.resolver = resolver;
    }

    @Override
    public double evalReal() {
        // try to resolve value if it's not resolved yet
        if (value == null && resolver != null) {
            resolver.resolve(this);
        }
        if (value == null) {
            throw new IllegalArgumentException("Placeholder is not resolved.");
        }
        return value;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public Placeholder getPlaceholderObj() {
        return new Placeholder(placeholder);
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlaceholderExpr that = (PlaceholderExpr) o;

        return !(placeholder != null ? !placeholder.equals(that.placeholder) : that.placeholder != null) && !(value != null ? !value.equals(that.value) : that.value != null);

    }

    @Override
    public int hashCode() {
        int result = placeholder != null ? placeholder.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
