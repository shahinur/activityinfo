package org.activityinfo.core.shared.expr;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import com.google.common.collect.Sets;
import org.activityinfo.core.shared.expr.functions.ArithmeticFunctions;
import org.activityinfo.core.shared.expr.functions.BooleanFunctions;

import java.util.Iterator;
import java.util.Set;

public class ExprParser {

    private static final Set<String> INFIX_OPERATORS = Sets.newHashSet("+", "-", "*", "/");

    private PeekingIterator<Token> lexer;
    private PlaceholderExprResolver placeholderExprResolver;

    public ExprParser(Iterator<Token> tokens) {
        this(tokens, null);
    }

    public ExprParser(Iterator<Token> tokens, PlaceholderExprResolver placeholderExprResolver) {
        this.lexer = Iterators.peekingIterator(Iterators.filter(tokens, new Predicate<Token>() {

            @Override
            public boolean apply(Token token) {
                return token.getType() != TokenType.WHITESPACE;
            }
        }));
        this.placeholderExprResolver = placeholderExprResolver;
    }

    public ExprNode parse() {
        ExprNode expr = parseSimple();
        if (!lexer.hasNext()) {
            return expr;
        }
        Token token = lexer.peek();
        if (isInfixOperator(token)) {
            lexer.next();
            ExprFunction<Double> function = ArithmeticFunctions.getBinaryInfix(token.getString());
            ExprNode right = parse();

            return new FunctionCallNode(function, expr, right);
        } else if (token.getType() == TokenType.BOOLEAN_OPERATOR) {
            lexer.next();
            ExprFunction<Boolean> function = BooleanFunctions.getBooleanFunction(token.getString());
            ExprNode right = parse();

            return new FunctionCallNode(function, expr, right);

        } else {
            return expr;
        }
    }
//	
//	throw new ExprSyntaxException(String.format("Expected +, -, /, or *, but found '%s' at %d",
//			token.getString(), token.getTokenStart()));


    private boolean isInfixOperator(Token token) {
        return token.getType().isSymbol() &&
                INFIX_OPERATORS.contains(token.getString());
    }

    public ExprNode parseSimple() {
        Token token = lexer.next();
        if (token.getType() == TokenType.PAREN_START) {
            return parseGroup();

        } else if (token.getType() == TokenType.BRACE_START) {
            return parsePlaceholder();

        } else if (token.getType() == TokenType.NUMBER) {
            return new ConstantExpr(Double.parseDouble(token.getString()));

        } else if (token.getType() == TokenType.BOOLEAN_LITERAL) {
            return new BooleanConstantExpr(Boolean.parseBoolean(token.getString()));

        } else if (token.getType() == TokenType.BOOLEAN_OPERATOR) {
            // unary NOT operation (right now it's the only case when we start with node fucntion)
            ExprFunction<Boolean> function = BooleanFunctions.getBooleanFunction(token.getString());
            ExprNode right = parse();
            return new FunctionCallNode(function, right);

        } else {
            throw new ExprSyntaxException("Unexpected token '" + token.getString() + "' at position " + token.getTokenStart() + "'");
        }
    }

    private ExprNode parseGroup() {
        ExprNode expr = parse();
        expectNext(TokenType.PAREN_END, "')'");
        return new GroupExpr(expr);
    }

    private ExprNode parsePlaceholder() {
        Token token = lexer.next();
        expectNext(TokenType.BRACE_END, "'}'");
        return new PlaceholderExpr(token.getString(), placeholderExprResolver);
    }

    /**
     * Retrieves the next token, and throws an exception if it does not match
     * the expected type.
     */
    private Token expectNext(TokenType expectedType, String description) {
        Token token = lexer.next();
        if (token.getType() != expectedType) {
            throw new ExprSyntaxException("Syntax error at " + token.getTokenStart() + ": expected " + description + " but found '" + token.getString() + "'");
        }
        return token;
    }

}
