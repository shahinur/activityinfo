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
import com.google.common.collect.Sets;
import org.activityinfo.core.shared.expr.*;
import org.activityinfo.core.shared.expr.constant.IsConstantExpr;
import org.activityinfo.core.shared.expr.functions.BooleanFunctions;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.ReferenceValue;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.model.type.primitive.TextValue;

import java.util.List;
import java.util.Set;

/**
 * @author yuriyz on 7/28/14.
 */
public class RowDataBuilder {

    public static final ExprFunction<Boolean, Boolean> DEFAULT_JOIN_FUNCTION = BooleanFunctions.AND;

    private List<RowData> rows = Lists.newArrayList(); // keep list, order is important!
    private FormClass formClass;

    public RowDataBuilder(FormClass formClass) {
        this.formClass = formClass;
    }

    public List<RowData> build(String skipExpression) {
        ExprLexer lexer = new ExprLexer(skipExpression);
        ExprParser parser = new ExprParser(lexer);
        ExprNode node = parser.parse();
        parse(node, DEFAULT_JOIN_FUNCTION);
        return rows;
    }

    private void parse(ExprNode node, ExprFunction joinFunction) {
        boolean wrappedByGroup = false;
        if (node instanceof GroupExpr) {
            node = ((GroupExpr) node).getExpr();
            wrappedByGroup = true;
        }

        // handle Function call node
        if (node instanceof FunctionCallNode && ((FunctionCallNode) node).getArguments().size() == 2) {
            final FunctionCallNode functionCallNode = (FunctionCallNode) node;
            final ExprNode arg1 = (ExprNode) functionCallNode.getArguments().get(0);
            final ExprNode arg2 = (ExprNode) functionCallNode.getArguments().get(1);

            if (arg1 instanceof PlaceholderExpr) {

                if (isFieldFunction(functionCallNode.getFunction())) {
                    final FormField field = formClass.getField(ResourceId.create(placeholder(arg1)));

                    final RowData row = getOrCreateRow(field);
                    row.setFunction(functionCallNode.getFunction());
                    row.setJoinFunction(joinFunction);

                    if (setValueInRow(row, arg2, field, wrappedByGroup)) {
                        return;
                    } else if (arg2 instanceof FunctionCallNode) {
                        final FunctionCallNode arg2Node = (FunctionCallNode) arg2;

                        if (isFieldFunction(arg2Node.getFunction())) {
                            parse(arg2Node, arg2Node.getFunction());
                            return;
                        } else {
                            // not field function -> means &&, || (but not ==, !=)

                            final ExprNode nestArg1 = (ExprNode) arg2Node.getArguments().get(0);
                            final ExprNode nestArg2 = (ExprNode) arg2Node.getArguments().get(1);

                            setValueInRow(row, nestArg1, field, false);

                            if (nestArg2 instanceof FunctionCallNode || nestArg2 instanceof GroupExpr) {
                                parse(nestArg2, arg2Node.getFunction());
                                return;
                            } else {
                                throw new UnsupportedOperationException();
                            }
                        }
                    }
                }
            }
        }
        throw new UnsupportedOperationException();
    }

    private RowData getOrCreateRow(FormField formField) {
        // search, maybe row is already present
        for (RowData row : rows) {
            if (row.getFormField().equals(formField)) {
                return row;
            }
        }

        // create new row
        RowData row = new RowData();
        row.setFormField(formField);
        rows.add(row);
        return row;
    }

    /**
     * Returns whether value was set in row or not.
     *
     * @param row       row
     * @param node      node
     * @param formField form field
     * @return Returns whether value was set in row or not
     */
    private static boolean setValueInRow(RowData row, ExprNode node, FormField formField, boolean wrappedByGroup) {
        if (node instanceof IsConstantExpr) {
            row.setValue(normalizeValue(node.evalReal(), formField.getType()));
            return true;

        } else if (node instanceof PlaceholderExpr) {
            ResourceId newItem = ResourceId.create(placeholder(node));

            if (row.getValue() instanceof ReferenceValue) { // update existing value
                ReferenceValue oldValue = (ReferenceValue)row.getValue();
                Set<ResourceId> newValue = Sets.newHashSet(oldValue.getResourceIds());
                newValue.add(newItem);
                row.setValue(new ReferenceValue(newValue));
            } else { // create value
                row.setValue(new ReferenceValue(newItem));
            }
            return true;
        }
        return false;
    }


    private static boolean isFieldFunction(ExprFunction exprFunction) {
        return exprFunction == BooleanFunctions.EQUAL || exprFunction == BooleanFunctions.NOT_EQUAL;
    }

    private static String placeholder(ExprNode node) {
        PlaceholderExpr placeholderExpr = (PlaceholderExpr) node;
        return placeholderExpr.getPlaceholder();
    }

    private static FieldValue normalizeValue(Object value, FieldType type) {
        if (value != null) {
            if (type instanceof TextType) {
                return TextValue.valueOf(value.toString());
            }
        }
        return null;
    }
}
