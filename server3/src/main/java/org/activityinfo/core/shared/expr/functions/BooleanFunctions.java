package org.activityinfo.core.shared.expr.functions;
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
 * MERCHANTABILITY or FITNESS NFOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import org.activityinfo.core.shared.expr.ExprFunction;
import org.activityinfo.core.shared.expr.ExprNode;

import java.util.List;

/**
 * @author yuriyz on 7/23/14.
 */
public class BooleanFunctions {

    public static final ExprFunction<Boolean, Boolean> AND = new ExprFunction<Boolean, Boolean>() {

        @Override
        public String getName() {
            return "AND";
        }

        @Override
        public Boolean applyReal(List<ExprNode<Boolean>> arguments) {
            Boolean result = arguments.get(0).evalReal();
            for (int i = 1; i < arguments.size(); i++) {
                result = result && arguments.get(i).evalReal();
            }
            return result;
        }
    };

    public static final ExprFunction<Boolean, Boolean> OR = new ExprFunction<Boolean, Boolean>() {

        @Override
        public String getName() {
            return "OR";
        }

        @Override
        public Boolean applyReal(List<ExprNode<Boolean>> arguments) {
            Boolean result = arguments.get(0).evalReal();
            for (int i = 1; i < arguments.size(); i++) {
                result = result || arguments.get(i).evalReal();
            }
            return result;
        }
    };

    public static final ExprFunction<Boolean, Object> EQUAL = new ExprFunction<Boolean, Object>() {

        @Override
        public String getName() {
            return "EQUAL";
        }

        @Override
        public Boolean applyReal(List<ExprNode<Object>> arguments) {
            int size = arguments.size();
            if (size <= 1) {
                throw new IllegalArgumentException();
            }
            Object result = arguments.get(0).evalReal();
            for (int i = 1; i < size; i++) {
                if (!result.equals(arguments.get(i).evalReal())) {
                    return false;
                }
            }
            return true;
        }
    };

    public static final ExprFunction<Boolean, Object> NOT_EQUAL = new ExprFunction<Boolean, Object>() {

        @Override
        public String getName() {
            return "NOT_EQUAL";
        }

        @Override
        public Boolean applyReal(List<ExprNode<Object>> arguments) {
            int size = arguments.size();
            if (size != 2) {
                throw new IllegalArgumentException();
            }
            return !arguments.get(0).evalReal().equals(arguments.get(1).evalReal());
        }
    };

    public static final ExprFunction<Boolean, Boolean> NOT = new ExprFunction<Boolean, Boolean>() {
        @Override
        public String getName() {
            return "not";
        }

        @Override
        public Boolean applyReal(List<ExprNode<Boolean>> arguments) {
            return !arguments.get(0).evalReal();
        }
    };

    public static ExprFunction<Boolean, ?> getBooleanFunction(String token) {
        if (token.equals("&&")) {
            return AND;

        } else if (token.equals("||")) {
            return OR;

        } else if (token.equals("!")) {
            return NOT;

        } else if (token.equals("==")) {
            return EQUAL;

        } else if (token.equals("!=")) {
            return NOT_EQUAL;

        } else {
            throw new IllegalArgumentException();
        }
    }
}
