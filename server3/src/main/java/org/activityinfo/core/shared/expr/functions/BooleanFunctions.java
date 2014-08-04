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

import com.google.common.collect.Maps;
import org.activityinfo.core.shared.expr.ExprFunction;
import org.activityinfo.core.shared.expr.ExprNode;
import org.activityinfo.core.shared.expr.constant.IsConstantExpr;
import org.activityinfo.model.type.number.Quantity;

import java.util.List;
import java.util.Map;

/**
 * @author yuriyz on 7/23/14.
 */
public class BooleanFunctions {

    public static final ExprFunction<Boolean, Boolean> AND = new ExprFunction<Boolean, Boolean>() {

        @Override
        public String getId() {
            return "&&";
        }

        @Override
        public String getLabel() {
            return "And";
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
        public String getId() {
            return "||";
        }

        @Override
        public String getLabel() {
            return "Or";
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
        public String getId() {
            return "==";
        }

        @Override
        public String getLabel() {
            return "Equal";
        }

        @Override
        public Boolean applyReal(List<ExprNode<Object>> arguments) {
            int size = arguments.size();
            if (size <= 1) {
                throw new IllegalArgumentException();
            }
            Object result = arguments.get(0).evalReal();
            for (int i = 1; i < size; i++) {
                ExprNode<Object> rightOperand = arguments.get(i);
                Object val = rightOperand.evalReal();
                val = cast(result, val, rightOperand);
                if (!result.equals(val)) {
                    return false;
                }
            }
            return true;
        }
    };

    public static final ExprFunction<Boolean, Object> NOT_EQUAL = new ExprFunction<Boolean, Object>() {

        @Override
        public String getId() {
            return "!=";
        }

        @Override
        public String getLabel() {
            return "Not equal";
        }

        @Override
        public Boolean applyReal(List<ExprNode<Object>> arguments) {
            int size = arguments.size();
            if (size != 2) {
                throw new IllegalArgumentException();
            }
            Object leftOperand = arguments.get(0).evalReal();
            ExprNode<Object> rightOperandNode = arguments.get(1);
            Object rightOperand = cast(leftOperand, rightOperandNode.evalReal(), rightOperandNode);
            return !leftOperand.equals(rightOperand);
        }
    };

    public static final ExprFunction<Boolean, Boolean> NOT = new ExprFunction<Boolean, Boolean>() {
        @Override
        public String getId() {
            return "!";
        }

        @Override
        public String getLabel() {
            return "Not";
        }

        @Override
        public Boolean applyReal(List<ExprNode<Boolean>> arguments) {
            return !arguments.get(0).evalReal();
        }
    };

    private static final Map<String, ExprFunction<Boolean, ?>> registry = Maps.newHashMap();

    static {
        registry.put(AND.getId(), AND);
        registry.put(OR.getId(), OR);
        registry.put(NOT.getId(), NOT);
        registry.put(EQUAL.getId(), EQUAL);
        registry.put(NOT_EQUAL.getId(), NOT_EQUAL);
    }

    public static ExprFunction<Boolean, ?> getBooleanFunction(String token) {
        return registry.get(token);
    }

    /**
     * Performs normalization for primitive FieldValues.
     * <p/>
     * Solution is error prone but is "good for now". Needs to be replaced in future...
     * <p/>
     * Example: {id} == 1. It's not clear which unit should be used for ConstanctExpr of 1. Therefore idea is
     * to "normalize" 1 and cast it to type of left operand. E.g. 1 apple == 1 is transformed to 1 apple == 1 apple.
     *
     * @param leftOperand  left operand
     * @param rightOperand right operand
     * @return normalized (casted) right operand
     */
    private static Object cast(Object leftOperand, Object rightOperand, ExprNode rightOperandNode) {
        if (rightOperandNode instanceof IsConstantExpr) {
            IsConstantExpr constantExpr = (IsConstantExpr) rightOperandNode;
            rightOperand = constantExpr.getValue();
            if (leftOperand instanceof Quantity && rightOperand instanceof Quantity) {
                ((Quantity) rightOperand).setUnits(((Quantity) leftOperand).getUnits());
            }
        }
        return rightOperand;
    }
}
