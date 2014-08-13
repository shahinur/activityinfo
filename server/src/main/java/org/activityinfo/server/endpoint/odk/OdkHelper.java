package org.activityinfo.server.endpoint.odk;

import org.activityinfo.core.shared.expr.ConstantExpr;
import org.activityinfo.core.shared.expr.ExprNode;
import org.activityinfo.core.shared.expr.ExprParser;
import org.activityinfo.core.shared.expr.FunctionCallNode;
import org.activityinfo.core.shared.expr.GroupExpr;
import org.activityinfo.core.shared.expr.SymbolExpr;
import org.activityinfo.core.shared.expr.functions.ExprFunction;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.primitive.BooleanFieldValue;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;

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

    static String convertRelevanceConditionExpression(String input) {
        if (input == null) return null;
        else return convertExprNodeToStringBuilder(ExprParser.parse(input)).toString();
    }

    private static StringBuilder convertExprNodeToStringBuilder(ExprNode exprNode) {
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
            stringBuilder.append(convertExprNodeToStringBuilder(arguments.get(0)));
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
            stringBuilder.append(convertExprNodeToStringBuilder(arguments.get(1)));

            return stringBuilder;
        } else if (exprNode instanceof GroupExpr) {
            GroupExpr groupExpr = (GroupExpr) exprNode;
            ExprNode expr = groupExpr.getExpr();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("(");
            stringBuilder.append(convertExprNodeToStringBuilder(expr));
            stringBuilder.append(")");
            return stringBuilder;
        } else if (exprNode instanceof SymbolExpr) {
            SymbolExpr symbolExpr = (SymbolExpr) exprNode;
            String name = symbolExpr.getName();
            StringBuilder stringBuilder = new StringBuilder();
            addAbsoluteFieldName(stringBuilder, name);
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
