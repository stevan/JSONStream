package org.example.jsonstream.tokenizer;

public class Scans {
    public enum TokenType {
        OPERATOR, KEYWORD, STRING, INTEGER, FLOAT, ERROR, END
    }
    
    public interface Scan {
        TokenType getType();
        String getValue();
        boolean isTerminal();
        
        default boolean isOperator() {
            return getType() == TokenType.OPERATOR;
        }
        default boolean isKeyword() {
            return getType() == TokenType.KEYWORD;
        }
        default boolean isError() {
            return getType() == TokenType.ERROR;
        }
        default boolean isEnd() {
            return getType() == TokenType.END;
        }
        default boolean isString() {
            return getType() == TokenType.STRING;
        }
        default boolean isInteger() {
            return getType() == TokenType.INTEGER;
        }
        default boolean isFloat() {
            return getType() == TokenType.FLOAT;
        }
        
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
        
    }
    // true, false, null
    public static class Keyword implements Scan {
        String keyword;
        public Keyword(String keyword) { this.keyword = keyword;}
        
        public TokenType getType() { return TokenType.KEYWORD; }
        public String getValue() { return keyword; }
        public boolean isTerminal() { return false; }
        @Override
        public String toString() { return "Keyword «" + getValue() + "»"; }
    }
    
    // {} [] , :
    public static class Operator implements Scan {
        String operator;
        public Operator(String operator) { this.operator = operator; }
    
        public TokenType getType() { return TokenType.OPERATOR; }
        public String getValue() { return operator; }
        public boolean isTerminal() { return false; }
        @Override
        public String toString() { return "Operator «" + getValue() + "»"; }
    }
    
    // 10, "Foo", 3.14
    public static class Constant implements Scan {
        String constant;
        TokenType type;
        public Constant(String constant, TokenType type) {
            this.constant = constant;
            this.type = type;
        }
        
        public TokenType getType() { return type; }
        public String getValue() { return constant; }
        public boolean isTerminal() { return false; }
        @Override
        public String toString() { return "Constant «" + getValue() + "»"; }
    }
    
    public static class Error implements Scan {
       String error;
       public Error(String error) { this.error = error; }
       
       public TokenType getType() { return TokenType.ERROR; }
       public String getValue() { return error; }
       public boolean isTerminal() { return true; }
       @Override
       public String toString() { return "Error «" + getValue() + "»"; }
    }
    
    public static class End implements Scan {
        public End() {}
        public TokenType getType() { return TokenType.END; }
        public String getValue() { return ""; }
        public boolean isTerminal() { return true; }
        @Override
        public String toString() { return "End «»"; }
    }
}
