package org.activityinfo.model.expr;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import com.google.common.collect.Sets;
import org.activityinfo.model.expr.functions.ExprFunction;
import org.activityinfo.model.expr.functions.ExprFunctions;

import java.util.Iterator;
import java.util.Set;

public class ExprParser {

    private static final Set<String> INFIX_OPERATORS = Sets.newHashSet("+", "-", "*", "/", "&&", "||", "==", "!=" );

    private static final Set<String> PREFIX_OPERATORS = Sets.newHashSet("!" );

    private PeekingIterator<Token> lexer;

    public ExprParser(Iterator<Token> tokens) {
        this.lexer = Iterators.peekingIterator(Iterators.filter(tokens, new Predicate<Token>() {

            @Override
            public boolean apply(Token token) {
                return token.getType() != TokenType.WHITESPACE && token.getType() != TokenType.STRING_START;
            }
        }));
    }

    public ExprNode parse() {
        ExprNode expr = parseSimple();
        if (!lexer.hasNext()) {
            return expr;
        }
        Token token = lexer.peek();
        if (isInfixOperator(token)) {
            lexer.next();
            ExprFunction function = ExprFunctions.get(token.getString());
            ExprNode right = parse();

            return new FunctionCallNode(function, expr, right);

        } else {
            return expr;
        }
    }

    private boolean isInfixOperator(Token token) {
        return token.getType() == TokenType.OPERATOR &&
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
            return new ConstantExpr(Boolean.parseBoolean(token.getString()));

        } else if (prefixOperator(token)) {
            ExprFunction function = ExprFunctions.get(token.getString());
            ExprNode right = parse();
            return new FunctionCallNode(function, right);

        } else if (token.getType() == TokenType.STRING_LITERAL) {
            return new ConstantExpr(token.getString());

        } else if (token.getType() == TokenType.SYMBOL) {
            return new SymbolExpr(token.getString());

        } else {
            throw new ExprSyntaxException("Unexpected token '" + token.getString() + "' at position " + token.getTokenStart() + "'");
        }
    }

    private boolean prefixOperator(Token token) {
        return token.getType() == TokenType.OPERATOR && PREFIX_OPERATORS.contains(token.getString());
    }

    private ExprNode parseGroup() {
        ExprNode expr = parse();
        expectNext(TokenType.PAREN_END, "')'");
        return new GroupExpr(expr);
    }

    private ExprNode parsePlaceholder() {
        Token token = lexer.next();
        expectNext(TokenType.BRACE_END, "'}'");
        return new SymbolExpr(token.getString());
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

    public static ExprNode parse(String expression) {
        ExprParser parser = new ExprParser(new ExprLexer(expression));
        return parser.parse();
    }
}
