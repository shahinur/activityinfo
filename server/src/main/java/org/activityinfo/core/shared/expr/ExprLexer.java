package org.activityinfo.core.shared.expr;

import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import org.activityinfo.core.shared.util.StringUtil;

import java.util.List;

/**
 * Splits an expression string into a sequence of tokens
 */
public class ExprLexer extends UnmodifiableIterator<Token> {

    private String string;
    private int currentCharIndex;
    private int currentTokenStart = 0;

    private static final String VALID_OPERATORS = "+-/*";

    public ExprLexer(String string) {
        this.string = string;
    }

    /**
     * @return the current character within the string being processed
     */
    private char peekChar() {
        return string.charAt(currentCharIndex);
    }

    private char nextChar() {
        return string.charAt(currentCharIndex++);
    }

    /**
     * Adds the current char to the current token
     */
    private void consumeChar() {
        currentCharIndex++;
    }

    private Token finishToken(TokenType type) {
        Token token = new Token(type, currentTokenStart,
                string.substring(currentTokenStart, currentCharIndex));
        currentTokenStart = currentCharIndex;
        return token;
    }

    public List<Token> readAll() {
        List<Token> tokens = Lists.newArrayList();
        while (!isEndOfInput()) {
            tokens.add(next());
        }
        return tokens;
    }

    public boolean isEndOfInput() {
        return currentCharIndex >= string.length();
    }

    @Override
    public boolean hasNext() {
        return !isEndOfInput();
    }

    @Override
    public Token next() {
        char c = nextChar();
        if (c == '(') {
            return finishToken(TokenType.PAREN_START);

        } else if (c == ')') {
            return finishToken(TokenType.PAREN_END);

        } else if (c == '{') {
            return finishToken(TokenType.BRACE_START);

        } else if (c == '}') {
            return finishToken(TokenType.BRACE_END);

        } else if (StringUtil.isWhitespace(c)) {
            return readWhitespace();

        } else if (isNumberPart(c)) {
            return readNumber();

        } else if (isOperator(c)) {
            return finishToken(TokenType.OPERATOR);

        } else if (isBooleanLiteral(c)) {
            return readBooleanLiteral(c);

        } else if (isBooleanOperator(c)) {
            return readBooleanOperator(c);

        } else if (isSymbolStart(c)) {
            return readSymbol();

        } else {
            throw new RuntimeException("Symbol '" + c + "' is not supported");
        }
    }

    private boolean isOperator(char c) {
        return VALID_OPERATORS.indexOf(c) != -1;
    }

    private boolean isBooleanOperator(char c) {
        if (c == '!') {
            return true;
        } else if (c == '&') {
            // if next char is also & then its && operator
            return string.charAt(currentCharIndex) == '&';
        } else if (c == '|') {
            // if next char is also | then its || operator
            return string.charAt(currentCharIndex) == '|';
        } else if (c == '=') {
            // if next char is also = then its == operator
            return string.charAt(currentCharIndex) == '=';
        }
        return false;
    }

    private boolean isSymbolStart(char c) {
        return c == '_' || Character.isLetter(c);
    }

    private boolean isSymbolChar(char c) {
        return c == '_' || StringUtil.isAlphabetic(c) || Character.isDigit(c);
    }

    private boolean isNumberPart(char c) {
        return Character.isDigit(c);
    }

    private boolean isBooleanLiteral(char c) {
        final int currentIndex = currentCharIndex - 1;
        if (c == 't' || c == 'T') {
            String trueLiteral = Boolean.TRUE.toString();
            String literal = string.substring(currentIndex, currentIndex + trueLiteral.length());
            return trueLiteral.equalsIgnoreCase(literal);
        } else if (c == 'f' || c == 'F') {
            String falseLiteral = Boolean.FALSE.toString();
            String literal = string.substring(currentIndex, currentIndex + falseLiteral.length());
            return falseLiteral.equalsIgnoreCase(literal);
        }
        return false;
    }

    private Token readWhitespace() {
        while (!isEndOfInput() && StringUtil.isWhitespace(peekChar())) {
            consumeChar();
        }
        return finishToken(TokenType.WHITESPACE);
    }

    private Token readNumber() {
        while (!isEndOfInput() && isNumberPart(peekChar())) {
            consumeChar();
        }
        return finishToken(TokenType.NUMBER);
    }

    private Token readSymbol() {
        while (!isEndOfInput() && isSymbolChar(peekChar())) {
            consumeChar();
        }
        return finishToken(TokenType.SYMBOL);
    }

    private Token readBooleanLiteral(char c) {
        currentCharIndex--;
        if (c == 't' || c == 'T') {
            String trueLiteral = Boolean.TRUE.toString();
            String literal = string.substring(currentCharIndex, currentCharIndex + trueLiteral.length());
            if (trueLiteral.equalsIgnoreCase(literal)) {
                currentCharIndex += trueLiteral.length();
                return finishToken(TokenType.BOOLEAN_LITERAL);
            }
        } else if (c == 'f' || c == 'F') {
            String falseLiteral = Boolean.FALSE.toString();
            String literal = string.substring(currentCharIndex, currentCharIndex + falseLiteral.length());
            if (falseLiteral.equalsIgnoreCase(literal)) {
                currentCharIndex += falseLiteral.length();
                return finishToken(TokenType.BOOLEAN_LITERAL);
            }
        }
        throw new RuntimeException("Bug in isBooleanLiteral() ?");
    }

    private Token readBooleanOperator(char c) {
        if (c == '!') {
            if (string.charAt(currentCharIndex) == '=') { // check whether it's NOT (!) or NOT_EQUAL operator (!=)
                currentCharIndex++;
            }
            return finishToken(TokenType.BOOLEAN_OPERATOR);
        } else if (c == '&') {
            // if next char is also & then its && operator
            currentCharIndex++;
            return finishToken(TokenType.BOOLEAN_OPERATOR);
        } else if (c == '|') {
            currentCharIndex++;
            // if next char is also | then its || operator
            return finishToken(TokenType.BOOLEAN_OPERATOR);
        } else if (c == '=') {
            currentCharIndex++;
            // if next char is also = then its == operator
            return finishToken(TokenType.BOOLEAN_OPERATOR);
        }
        throw new RuntimeException("Invalid boolean operator.");
    }

}
