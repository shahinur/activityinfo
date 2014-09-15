package org.activityinfo.service.tables.function;

import org.activityinfo.model.expr.functions.ExprFunction;
import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.table.ColumnView;

import java.util.List;

public class ColumnFunctions {


    public static ColumnView create(ExprFunction fn, List<ColumnView> arguments) {
        if(fn.getId().equals("==")) {
            if (argsMatch(arguments, ColumnType.STRING, ColumnType.STRING)) {
                return new StringComparisonView(arguments.get(0), arguments.get(1), ComparisonOp.EQUALS);
            }
        }else if(fn.getId().equals("!=")) {
            if(argsMatch(arguments, ColumnType.STRING, ColumnType.STRING)) {
                return new StringComparisonView(arguments.get(0), arguments.get(1), ComparisonOp.NOT_EQUALS);
            }
        } else if(fn.getId().equals("+")) {
            if(argsMatch(arguments, ColumnType.NUMBER, ColumnType.NUMBER)) {
                return new DoubleBinaryOpView(arguments.get(0), arguments.get(1));
            }
        } else if(fn.getId().equals("&&")) {
            if(argsMatch(arguments, ColumnType.BOOLEAN, ColumnType.BOOLEAN)) {
                return new BooleanBinaryOp(arguments.get(0), arguments.get(1));
            }
        }
        throw new UnsupportedOperationException("Unimplemented function: " + fn.getId());
    }

    private static boolean argsMatch(List<ColumnView> arguments, ColumnType... argumentTypes) {
        if(arguments.size() != argumentTypes.length) {
            return false;
        }
        for(int i=0;i!=argumentTypes.length;++i) {
            if(arguments.get(i).getType() != argumentTypes[i]) {
                return false;
            }
        }
        return true;
    }
}
