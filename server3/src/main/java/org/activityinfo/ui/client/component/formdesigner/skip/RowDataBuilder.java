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
import org.activityinfo.model.type.TextType;
import org.activityinfo.model.type.enumerated.EnumType;

import java.util.HashSet;
import java.util.List;

/**
 * @author yuriyz on 7/28/14.
 */
public class RowDataBuilder {

    public static final ExprFunction<Boolean,Boolean> DEFAULT_JOIN_FUNCTION = BooleanFunctions.AND;

    private FormClass formClass;

    public RowDataBuilder(FormClass formClass) {
        this.formClass = formClass;
    }

    public List<RowData> build(String skipExpression) {
        List<RowData> result = Lists.newArrayList();
        ExprLexer lexer = new ExprLexer(skipExpression);
        ExprParser parser = new ExprParser(lexer);
        ExprNode node = parser.parse();
        parse(node, result, DEFAULT_JOIN_FUNCTION);
        return result;
    }

    private void parse(ExprNode node, List<RowData> result, ExprFunction joinFunction) {
        if (node instanceof FunctionCallNode) {
            FunctionCallNode functionCallNode = (FunctionCallNode) node;
            List arguments = functionCallNode.getArguments();

            if (arguments.size() == 2) {
                if (arguments.get(0) instanceof PlaceholderExpr) {

                    PlaceholderExpr placeholderExpr = (PlaceholderExpr) arguments.get(0);
                    FormField field = formClass.getField(ResourceId.create(placeholderExpr.getPlaceholder()));

                    if (arguments.get(1) instanceof IsConstantExpr) {
                        ExprNode secondArgument = (ExprNode) arguments.get(1);

                        Object literalValue = normalizeValue(secondArgument.evalReal(), field.getType());

                        RowData row = new RowData();
                        row.setValue(literalValue);
                        row.setFormField(field);
                        row.setFunction(functionCallNode.getFunction());
                        row.setJoinFunction(joinFunction);
                        result.add(row);
                        return;
                    } else if (arguments.get(1) instanceof PlaceholderExpr) {
                        PlaceholderExpr secondArgument = (PlaceholderExpr) arguments.get(1);

                        RowData row = new RowData();
                        row.setValue(Sets.newHashSet(ResourceId.create(secondArgument.getPlaceholder())));
                        row.setFormField(field);
                        row.setFunction(functionCallNode.getFunction());
                        row.setJoinFunction(joinFunction);
                        result.add(row);
                        return;
                    } else if (arguments.get(1) instanceof FunctionCallNode) {
                        FunctionCallNode nestedFunctionCall = (FunctionCallNode) arguments.get(1);
                        List nestedArguments = nestedFunctionCall.getArguments();
                        if (nestedArguments.get(0) instanceof PlaceholderExpr && nestedArguments.get(1) instanceof FunctionCallNode
                                && field.getType() instanceof EnumType ) {
                            PlaceholderExpr secondArgument = (PlaceholderExpr) nestedArguments.get(0);

                            RowData row = new RowData();
                            row.setValue(Sets.newHashSet(ResourceId.create(secondArgument.getPlaceholder())));
                            row.setFormField(field);
                            row.setFunction(functionCallNode.getFunction());
                            row.setJoinFunction(joinFunction);
                            result.add(row);

                            parse((ExprNode) nestedArguments.get(1), result, nestedFunctionCall.getFunction());
                            return;
                        } else
                        if (nestedArguments.get(0) instanceof PlaceholderExpr && nestedArguments.get(1) instanceof GroupExpr && ((GroupExpr)nestedArguments.get(1)).getExpr() instanceof FunctionCallNode) {
                            PlaceholderExpr firstArgument = (PlaceholderExpr) nestedArguments.get(0);
                            FunctionCallNode groupArgument = (FunctionCallNode) ((GroupExpr) nestedArguments.get(1)).getExpr();

                            field = formClass.getField(ResourceId.create(((PlaceholderExpr)groupArgument.getArguments().get(0)).getPlaceholder()));

                            HashSet<ResourceId> enumValues = Sets.newHashSet(ResourceId.create(firstArgument.getPlaceholder()));

                            RowData row = new RowData();
                            row.setValue(enumValues);
                            row.setFormField(field);
                            row.setFunction(groupArgument.getFunction());
                            row.setJoinFunction(joinFunction);
                            result.add(row);

                            handleEnumValues(field, enumValues, groupArgument);

                            //parse((ExprNode) nestedArguments.get(1), result, nestedFunctionCall.getFunction());
                            return;
                        }
                    }
                }
            }
            throw new UnsupportedOperationException();

        }
    }

    private static void handleEnumValues(FormField formField, HashSet<ResourceId> enumValues, FunctionCallNode node) {
        Object arg1 = node.getArguments().get(0);
        Object arg2 = node.getArguments().get(1);
        if (arg1 instanceof PlaceholderExpr) {
            PlaceholderExpr placeholderArg1 = (PlaceholderExpr) arg1;
            if (placeholderArg1.getPlaceholder().equals(formField.getId().asString())) {
                if (arg2 instanceof PlaceholderExpr) {
                    enumValues.add(ResourceId.create(((PlaceholderExpr)arg2).getPlaceholder()));
                    return;
                }
                handleEnumValues(formField, enumValues, (FunctionCallNode) arg2);
            } else if (arg2 instanceof FunctionCallNode) {
                enumValues.add(ResourceId.create(placeholderArg1.getPlaceholder()));
                handleEnumValues(formField, enumValues, (FunctionCallNode) arg2);
            } else if (arg2 instanceof PlaceholderExpr) {
                enumValues.add(ResourceId.create(((PlaceholderExpr)arg2).getPlaceholder()));
            }
        }
    }

    private Object normalizeValue(Object value, FieldType type) {
        if (value != null) {
            if (type instanceof TextType) {
                return value.toString();
            }
        }
        return value;
    }
}
