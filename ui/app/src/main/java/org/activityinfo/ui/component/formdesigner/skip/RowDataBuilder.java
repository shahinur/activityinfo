package org.activityinfo.ui.component.formdesigner.skip;
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
import org.activityinfo.model.expr.*;
import org.activityinfo.model.expr.functions.BooleanFunctions;
import org.activityinfo.model.expr.functions.ExprFunction;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.model.type.ReferenceValue;
import org.activityinfo.model.type.enumerated.EnumFieldValue;
import org.activityinfo.model.type.enumerated.EnumType;

import java.util.List;
import java.util.Set;

/**
 * @author yuriyz on 7/28/14.
 */
public class RowDataBuilder {

    public static final ExprFunction DEFAULT_JOIN_FUNCTION = BooleanFunctions.AND;

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
        if (node instanceof GroupExpr) {
            node = ((GroupExpr) node).getExpr();
        }

        // handle Function call node
        if (node instanceof FunctionCallNode && ((FunctionCallNode) node).getArguments().size() == 2) {
            final FunctionCallNode functionCallNode = (FunctionCallNode) node;
            ExprNode arg1 = unwrap(functionCallNode.getArguments().get(0));
            ExprNode arg2 = unwrap(functionCallNode.getArguments().get(1));


            if (arg1 instanceof SymbolExpr) {

                if (isFieldFunction(functionCallNode.getFunction())) {
                    final FormField field = formClass.getField(ResourceId.valueOf(placeholder(arg1)));

                    final RowData row = getOrCreateRow(field);
                    row.setFunction(functionCallNode.getFunction());
                    row.setJoinFunction(joinFunction);

                    if (setValueInRow(row, arg2)) {
                        return;
                    } else if (arg2 instanceof FunctionCallNode) {
                        final FunctionCallNode arg2Node = (FunctionCallNode) arg2;

                        if (isFieldFunction(arg2Node.getFunction())) {
                            parse(arg2Node, arg2Node.getFunction());
                            return;
                        } else {
                            // not field function -> means &&, || (but not ==, !=)

                            final ExprNode nestArg1 = arg2Node.getArguments().get(0);
                            final ExprNode nestArg2 = arg2Node.getArguments().get(1);

                            setValueInRow(row, nestArg1);

                            if (nestArg2 instanceof FunctionCallNode || nestArg2 instanceof GroupExpr) {
                                parse(nestArg2, arg2Node.getFunction());
                                return;
                            } else {
                                throw new UnsupportedOperationException();
                            }
                        }
                    }
                }
            } else if (arg1 instanceof FunctionCallNode) {
                // parse flat structure
                for (Object exprNode : functionCallNode.getArguments()) {
                    FunctionCallNode unwrappedNode = (FunctionCallNode) unwrap((ExprNode) exprNode);
                    if (isFieldFunction(unwrappedNode.getFunction())) {
                        ExprNode unwrappedArg1 = unwrappedNode.getArguments().get(0);
                        ExprNode unwrappedArg2 = unwrappedNode.getArguments().get(1);

                        final FormField field = formClass.getField(ResourceId.valueOf(placeholder(unwrappedArg1)));

                        final RowData row = getOrCreateRow(field);
                        row.setFunction(unwrappedNode.getFunction());
                        row.setJoinFunction(functionCallNode.getFunction());

                        setValueInRow(row, unwrappedArg2);
                    } else {
                        parse(unwrappedNode, unwrappedNode.getFunction());
                    }
                }
                return;
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

    private static ExprNode unwrap(ExprNode node) {
        if (node instanceof GroupExpr) {
            return ((GroupExpr) node).getExpr();
        }
        return node;
    }

    /**
     * Returns whether value was set in row or not.
     *
     * @param row  row
     * @param node node
     * @return Returns whether value was set in row or not
     */
    private static boolean setValueInRow(RowData row, ExprNode node) {
        if (node instanceof ConstantExpr) {
            row.setValue(((ConstantExpr) node).getValue());
            return true;

        } else if (node instanceof SymbolExpr) {
            ResourceId newItem = ResourceId.valueOf(placeholder(node));

            if (row.getValue() instanceof ReferenceValue) { // update existing value
                ReferenceValue oldValue = (ReferenceValue) row.getValue();
                Set<ResourceId> newValue = Sets.newHashSet(oldValue.getResourceIds());
                newValue.add(newItem);
                row.setValue(new ReferenceValue(newValue));
            } else { // create value
                if (row.getFormField().getType() instanceof EnumType) {
                    row.setValue(new EnumFieldValue(newItem));
                } else if (row.getFormField().getType() instanceof ReferenceType) {
                    row.setValue(new ReferenceValue(newItem));
                } else {
                    throw new UnsupportedOperationException(row.getFormField().getType() + " is not supported.");
                }
            }
            return true;
        }
        return false;
    }


    private static boolean isFieldFunction(ExprFunction exprFunction) {
        return exprFunction == BooleanFunctions.EQUAL || exprFunction == BooleanFunctions.NOT_EQUAL;
    }

    private static String placeholder(ExprNode node) {
        SymbolExpr symbolExpr = (SymbolExpr) node;
        return symbolExpr.getName();
    }
}
