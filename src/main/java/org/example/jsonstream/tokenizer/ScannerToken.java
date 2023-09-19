package org.example.jsonstream.tokenizer;

public interface ScannerToken {
    enum TokenType {
        OPERATOR, KEYWORD, STRING, INTEGER, FLOAT, ERROR, END
    }
    
    TokenType getType();
    String getValue();
    boolean isTerminal();
    
    default boolean isOperator() { return getType() == TokenType.OPERATOR; }
    default boolean isKeyword() { return getType() == TokenType.KEYWORD; }
    default boolean isError() { return getType() == TokenType.ERROR; }
    default boolean isEnd() { return getType() == TokenType.END; }
    default boolean isString() { return getType() == TokenType.STRING; }
    default boolean isInteger() { return getType() == TokenType.INTEGER; }
    default boolean isFloat() { return getType() == TokenType.FLOAT; }
    default boolean isNumber() {
        return getType() == TokenType.FLOAT
            || getType() == TokenType.INTEGER;
    }
    default boolean isConstant() {
        TokenType type = getType();
        return type == TokenType.STRING
                   || type == TokenType.INTEGER
                   || type == TokenType.FLOAT;
    }
    
    // true, false, null
    static ScannerToken keyword(String keyword) {
        return new ScannerToken() {
            public TokenType getType() { return TokenType.KEYWORD; }
            public String getValue() { return keyword; }
            public boolean isTerminal() { return false; }
            @Override
            public String toString() { return "Keyword «" + getValue() + "»"; }
        };
    }
    
    // {} [] , :
    static ScannerToken operator(String operator) {
        return new ScannerToken() {
            public TokenType getType() { return TokenType.OPERATOR; }
            public String getValue() { return operator; }
            public boolean isTerminal() { return false; }
            @Override
            public String toString() { return "Operator «" + getValue() + "»"; }
        };
    }
    
    // 10, "Foo", 3.14
    static ScannerToken constant(String constant, TokenType type) {
        return new ScannerToken() {
            public TokenType getType() { return type; }
            public String getValue() { return constant; }
            public boolean isTerminal() { return false; }
            @Override
            public String toString() { return "Constant «" + getValue() + "»"; }
        };
    }
    
    static ScannerToken error(String error) {
        return new ScannerToken() {
            public TokenType getType() { return TokenType.ERROR; }
            public String getValue() { return error; }
            public boolean isTerminal() { return true; }
            @Override
            public String toString() { return "Error «" + getValue() + "»"; }
        };
    }
    
    static ScannerToken end() {
        return new ScannerToken() {
            public TokenType getType() { return TokenType.END; }
            public String getValue() { return ""; }
            public boolean isTerminal() { return true; }
            @Override
            public String toString() { return "End «»"; }
        };
    }
}
