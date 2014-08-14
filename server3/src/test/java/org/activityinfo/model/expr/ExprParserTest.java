package org.activityinfo.model.expr;


import org.activityinfo.model.expr.functions.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ExprParserTest {


    @Test
    public void parseSimple() {
        expect("1", new ConstantExpr(1));
        expect("(1)", new GroupExpr(new ConstantExpr(1)));
        expect("1+2", new FunctionCallNode(PlusFunction.INSTANCE,
                new ConstantExpr(1),
                new ConstantExpr(2)));
    }

    @Test
    public void parseEqualsSign() {
        expect("true==false", new FunctionCallNode(
                EqualFunction.INSTANCE,
                new ConstantExpr(true),
                new ConstantExpr(false)));
    }

    @Test
    public void parseBooleanSimple() {
        expect("true", new ConstantExpr(true));
        expect("false", new ConstantExpr(false));
        expect("true&&false&&false", new FunctionCallNode(BooleanFunctions.AND,
                new ConstantExpr(true),
                new FunctionCallNode(BooleanFunctions.AND,
                        new ConstantExpr(false),
                        new ConstantExpr(false)
                )
        ));
    }


    @Test
    public void parseNested() {
        expect("(1+2)/3",
                new FunctionCallNode(ArithmeticFunctions.DIVIDE,
                        new GroupExpr(
                                new FunctionCallNode(ArithmeticFunctions.BINARY_PLUS,
                                        new ConstantExpr(1),
                                        new ConstantExpr(2))),
                        new ConstantExpr(3)));
    }

    @Test
    public void parseSymbols() {
        expect("{i1}+{i2}+1", new FunctionCallNode(ArithmeticFunctions.BINARY_PLUS,
                new SymbolExpr("i1"),
                new FunctionCallNode(ArithmeticFunctions.BINARY_PLUS, new SymbolExpr("i2"),
                        new ConstantExpr(1))));

        expect("({class1_i1}+{class2_i2})/{class3_i3}",
                new FunctionCallNode(ArithmeticFunctions.DIVIDE,
                        new GroupExpr(
                                new FunctionCallNode(ArithmeticFunctions.BINARY_PLUS,
                                        new SymbolExpr("class1_i1"),
                                        new SymbolExpr("class2_i2"))
                        ), new SymbolExpr("class3_i3")));

        expect("{s000002_i0009ls}+{s000002_i0009lt}",
                new FunctionCallNode(ArithmeticFunctions.BINARY_PLUS,
                        new SymbolExpr("s000002_i0009ls"),
                        new SymbolExpr("s000002_i0009lt"))
        );
    }

    @Test
    public void parseComparisons() {
        expect("A==B", new FunctionCallNode(EqualFunction.INSTANCE,
                new SymbolExpr("A"),
                new SymbolExpr("B")));
    }


    private void expect(String string, ExprNode expr) {
        System.out.println("Parsing [" + string + "]");
        ExprLexer lexer = new ExprLexer(string);
        ExprParser parser = new ExprParser(lexer);
        ExprNode actual = parser.parse();

        assertEquals(expr, actual);
    }

}
