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

import com.google.common.collect.Lists;
import org.activityinfo.core.shared.expr.*;
import org.activityinfo.core.shared.expr.constant.BooleanConstantExpr;
import org.activityinfo.core.shared.expr.constant.NumberConstantExpr;
import org.activityinfo.core.shared.expr.constant.StringConstantExpr;
import org.activityinfo.core.shared.expr.functions.BooleanFunctions;
import org.activityinfo.model.resource.ResourceId;

import java.util.List;
import java.util.Set;

/**
 * @author yuriyz on 7/25/14.
 */
public class ExpressionBuilder {

    private List<RowData> rows;

    public ExpressionBuilder(List<RowData> rows) {
        this.rows = rows;
    }

    public String build() {
        return buildNode(null, 0).asExpression();
    }

    private ExprNode buildNode(ExprNode leftNode, int index) {
        RowData row = rows.get(index);

        ExprNode left = leftNode != null ? leftNode : new PlaceholderExpr(row.getFormField().getId().asString());

        ExprNode right = null;

        Object value = row.getValue();

        if (value instanceof ResourceId) {
            right = new PlaceholderExpr(row.getFormField().getId().asString());
        } else if (value instanceof Boolean) {
            right = new BooleanConstantExpr((Boolean) value);
        } else if (value instanceof Double) {
            right = new NumberConstantExpr((Double) value);
        } else if (value instanceof String) {
            right = new StringConstantExpr((String) value);
        } else if (value instanceof Set) {
            List<ResourceId> idSet = Lists.newArrayList((Set<ResourceId>) value);
            int size = idSet.size();
            if (size == 1) {
                right = new PlaceholderExpr(idSet.get(0).asString());
            } else {
                return new GroupExpr(new FunctionCallNode(row.getFunction(), left, buildNodeForSet(idSet, 0, row)));
            }
        } else {
            throw new UnsupportedOperationException("Not supported value: " + value);
        }

        if ((index + 1) < rows.size()) {
            index++;
            RowData nextRow = rows.get(index);
            right = new FunctionCallNode(nextRow.getJoinFunction(), right,  buildNode(new PlaceholderExpr(nextRow.getFormField().getId().asString()), index));
        }
        return new FunctionCallNode(row.getFunction(), left, right);
    }

    private ExprNode buildNodeForSet(List<ResourceId> values, int index, RowData row) {


        ExprFunction internalFunction = BooleanFunctions.OR;
        if (row.getFunction() == BooleanFunctions.NOT_EQUAL) {
            internalFunction = BooleanFunctions.AND;
        }

        ExprNode left = new PlaceholderExpr(values.get(index).asString());
        ExprNode innerLeft = new PlaceholderExpr(row.getFormField().getId().asString());
        ExprNode innerRight;

        index++; // increase index before handling inner expression
        if ((index + 1) == values.size()) {
            innerRight = new PlaceholderExpr(values.get(index).asString());
        } else {
            innerRight = buildNodeForSet(values, index, row);
        }
        ExprNode right = new FunctionCallNode(row.getFunction(), innerLeft, innerRight);
        return new FunctionCallNode(internalFunction, left, right);
    }

//    private void handleRow(RowData row, int index) {
//        if (index != 0) {
//            expression += row.getJoinFunction().getId();
//        }
//
//        // field id
//        expression += "{" + row.getFormField().getId().asString() + "}";
//
//        // boolean function
//        expression += row.getFunction().getId();
//
//        // value
//        Object value = row.getValue();
//        if (value instanceof ResourceId) {
//            expression += "{" + ((ResourceId) value).asString() + "}";
//        } else if (value instanceof Boolean || value instanceof Number) {
//            expression += value;
//        } else if (value instanceof String) {
//            expression += "\"" + value + "\"";
//        } else if (value instanceof Set) {
//            List<ResourceId> idSet = Lists.newArrayList((Set<ResourceId>) value);
//            int size = idSet.size();
//            for (int i = 0; i < size; i++) {
//                ResourceId resourceId = idSet.get(i);
//
//                if (size > 1 && i == 0) { // wrap last statement into parenthesis if there is more than one resourceId
//                    int lastPlaceholderStartChart = expression.lastIndexOf("{");
//                    expression = expression.substring(0, lastPlaceholderStartChart) + "(" + expression.substring(lastPlaceholderStartChart);
//                }
//
//                // first element
//                if (i == 0) {
//                    expression += "{" + resourceId.asString() + "}";
//                } else { // for all other elements need to build complete statement
//                    String fieldIdPlaceholder = "{" + row.getFormField().getId().asString() + "}";
//                    expression += "||" + fieldIdPlaceholder + row.getFunction().getId() + "{" + resourceId.asString() + "}";
//                }
//
//                if (size > 1 && i == (size - 1)) { // wrap last statement into parenthesis if there is more than one resourceId
//                    expression += ")";
//                }
//            }
//        } else {
//            throw new UnsupportedOperationException("Not supported value: " + value);
//        }
//    }
}
