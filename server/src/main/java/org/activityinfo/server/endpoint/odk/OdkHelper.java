package org.activityinfo.server.endpoint.odk;

import com.google.common.collect.Sets;
import org.activityinfo.model.expr.ConstantExpr;
import org.activityinfo.model.expr.ExprNode;
import org.activityinfo.model.expr.ExprParser;
import org.activityinfo.model.expr.FunctionCallNode;
import org.activityinfo.model.expr.GroupExpr;
import org.activityinfo.model.expr.SymbolExpr;
import org.activityinfo.model.expr.functions.ExprFunction;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.primitive.BooleanFieldValue;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;
import java.util.Set;

class OdkHelper {

    static String extractText(Node node) {
        NodeList childNodes = node.getChildNodes();

        if (childNodes.getLength() == 1) {
            Node child = childNodes.item(0);
            if (child.getChildNodes().getLength() == 0 && "#text".equals(child.getNodeName())) {
                return child.getNodeValue();
            }
        }

        return null;
    }



}
