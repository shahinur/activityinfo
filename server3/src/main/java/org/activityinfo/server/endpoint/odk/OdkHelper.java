package org.activityinfo.server.endpoint.odk;


import com.google.common.collect.Sets;
import org.activityinfo.model.expr.*;
import org.activityinfo.model.expr.functions.ExprFunction;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.primitive.BooleanFieldValue;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;
import java.util.Set;

class OdkHelper {
    static String toAbsoluteFieldName(String name) {
        return addAbsoluteFieldName(new StringBuilder(), name).toString();
    }

    static String toRelativeFieldName(String name) {
        return addRelativeFieldName(new StringBuilder(), name).toString();
    }

    static String extractText(Node node) {
        NodeList childNodes = node.getChildNodes();

        if (childNodes.getLength() == 0) return "";

        if (childNodes.getLength() == 1) {
            Node child = childNodes.item(0);
            if (child.getChildNodes().getLength() == 0 && "#text".equals(child.getNodeName())) {
                return child.getNodeValue();
            }
        }

        return null;
    }

    static Set<String> extractFieldsSet(List<FormField> formFields) {
        Set<String> fieldsSet = Sets.newHashSetWithExpectedSize(formFields.size());

        for (FormField formField : formFields) {
            fieldsSet.add(formField.getId().asString());
        }

        return fieldsSet;
    }

    static String convertRelevanceConditionExpression(String input, Set<String> fieldsSet) {
        if (input == null) return null;
        else return convertExprNodeToStringBuilder(ExprParser.parse(input), fieldsSet).toString();
    }

    private static StringBuilder convertExprNodeToStringBuilder(ExprNode exprNode, Set<String> fieldsSet) {
        if (exprNode instanceof ConstantExpr) {
            ConstantExpr constantExpr = (ConstantExpr) exprNode;
            FieldValue value = constantExpr.getValue();
            if (value instanceof BooleanFieldValue) {
                BooleanFieldValue booleanFieldValue = (BooleanFieldValue) value;
                return new StringBuilder(booleanFieldValue.asBoolean() ? "true()" : "false()");
            } else return new StringBuilder(constantExpr.asExpression());
        } else if (exprNode instanceof FunctionCallNode) {
            FunctionCallNode functionCallNode = (FunctionCallNode) exprNode;
            List<ExprNode> arguments = functionCallNode.getArguments();
            ExprFunction function = functionCallNode.getFunction();

            //TODO Make sure all operators are supported, not just binary ones (this also goes for asExpression())
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(convertExprNodeToStringBuilder(arguments.get(0), fieldsSet));
            stringBuilder.append(" ");

            switch (function.getId()) {
                case "&&":
                    stringBuilder.append("and");
                    break;
                case "==":
                    stringBuilder.append("=");
                    break;
                case "||":
                    stringBuilder.append("or");
                    break;
                default:
                    stringBuilder.append(function.getId());
            }

            stringBuilder.append(" ");
            stringBuilder.append(convertExprNodeToStringBuilder(arguments.get(1), fieldsSet));

            return stringBuilder;
        } else if (exprNode instanceof GroupExpr) {
            GroupExpr groupExpr = (GroupExpr) exprNode;
            ExprNode expr = groupExpr.getExpr();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("(");
            stringBuilder.append(convertExprNodeToStringBuilder(expr, fieldsSet));
            stringBuilder.append(")");
            return stringBuilder;
        } else if (exprNode instanceof SymbolExpr) {
            SymbolExpr symbolExpr = (SymbolExpr) exprNode;
            String name = symbolExpr.getName();
            StringBuilder stringBuilder = new StringBuilder();

            if (fieldsSet.contains(name)) {
                addAbsoluteFieldName(stringBuilder, name);
            } else {
                stringBuilder.append("'");
                stringBuilder.append(name);
                stringBuilder.append("'");
            }

            return stringBuilder;
        } else throw new UnsupportedOperationException();
    }

    private static StringBuilder addRelativeFieldName(StringBuilder stringBuilder, String name) {
        stringBuilder.append("field_");
        stringBuilder.append(name);
        return stringBuilder;
    }

    private static StringBuilder addAbsoluteFieldName(StringBuilder stringBuilder, String name) {
        stringBuilder.append("/data/");
        return addRelativeFieldName(stringBuilder, name);
    }
}
