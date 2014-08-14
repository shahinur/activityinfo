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
import org.activityinfo.model.expr.*;
import org.activityinfo.model.expr.functions.*;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.ReferenceValue;
import org.activityinfo.model.type.number.Quantity;
import org.activityinfo.model.type.primitive.BooleanFieldValue;
import org.activityinfo.model.type.primitive.TextValue;

import java.util.List;

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

        ExprNode left = leftNode != null ? leftNode : new SymbolExpr(row.getFormField().getId().asString());

        ExprNode right = null;

        FieldValue value = row.getValue();

        if (value instanceof BooleanFieldValue) {
            right = new ConstantExpr(value);
        } else if (value instanceof Quantity) {
            right = new ConstantExpr(value);
        } else if (value instanceof TextValue) {
            right = new ConstantExpr(value);
        } else if (value instanceof ReferenceValue) {
            List<ResourceId> idSet = Lists.newArrayList(((ReferenceValue)value).getResourceIds());
            int size = idSet.size();
            if (size == 1) {
                right = new SymbolExpr(idSet.get(0).asString());
            } else {
                return new GroupExpr(buildNodeForSet(left, idSet, row));
            }
        } else {
            throw new UnsupportedOperationException("Not supported value: " + value);
        }

        ExprNode node = new FunctionCallNode(row.getFunction(), left, right);
        if (rows.size() > 1) {
            node = new GroupExpr(node);
        }

        if ((index + 1) < rows.size()) {
            index++;
            RowData nextRow = rows.get(index);

            return new FunctionCallNode(nextRow.getJoinFunction(), node,  buildNode(null, index));
        }

        return node;
    }

    private ExprNode buildNodeForSet(ExprNode left, List<ResourceId> values, RowData row) {
        ExprFunction internalFunction = BooleanFunctions.OR;
        if (row.getFunction() == BooleanFunctions.NOT_EQUAL) {
            internalFunction = BooleanFunctions.AND;
        }

        final List<ExprNode> arguments = Lists.newArrayList();
        for (ResourceId value : values) {
            arguments.add(new GroupExpr(new FunctionCallNode(row.getFunction(), left, new SymbolExpr(value.asString()))));
        }

        return new FunctionCallNode(internalFunction, arguments);
    }
}
