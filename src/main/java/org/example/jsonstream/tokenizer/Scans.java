package org.example.jsonstream.tokenizer;

public class Scans {
    public enum ScanType {
        OPERATOR, KEYWORD, STRING, INTEGER, FLOAT, ERROR, END
    }
    
    public interface Scan {
        ScanType getType();
        String getValue();
        boolean isTerminal();
        
        default boolean isOperator() { return getType() == ScanType.OPERATOR; }
        default boolean isKeyword()  { return getType() == ScanType.KEYWORD;  }
        default boolean isError()    { return getType() == ScanType.ERROR;    }
        default boolean isEnd()      { return getType() == ScanType.END;      }
        default boolean isString()   { return getType() == ScanType.STRING;   }
        default boolean isInteger()  { return getType() == ScanType.INTEGER;  }
        default boolean isFloat()    { return getType() == ScanType.FLOAT;    }
        
        default boolean isNumber() {
            ScanType type = getType();
            return type == ScanType.FLOAT || type == ScanType.INTEGER;
        }
        
        default boolean isConstant() {
            ScanType type = getType();
            return type == ScanType.STRING
                || type == ScanType.INTEGER
                || type == ScanType.FLOAT;
        }
        
    }
    // true, false, null
    public static class Keyword implements Scan {
        String keyword;
        public Keyword(String keyword) { this.keyword = keyword;}
        
        public ScanType getType() { return ScanType.KEYWORD; }
        public String getValue() { return keyword; }
        public boolean isTerminal() { return false; }
        @Override
        public String toString() { return "Keyword «" + getValue() + "»"; }
    }
    
    // {} [] , :
    public static class Operator implements Scan {
        String operator;
        public Operator(String operator) { this.operator = operator; }
    
        public ScanType getType() { return ScanType.OPERATOR; }
        public String getValue() { return operator; }
        public boolean isTerminal() { return false; }
        @Override
        public String toString() { return "Operator «" + getValue() + "»"; }
    }
    
    // 10, "Foo", 3.14
    public static class Constant implements Scan {
        String constant;
        ScanType type;
        public Constant(String constant, ScanType type) {
            this.constant = constant;
            this.type = type;
        }
        
        public ScanType getType() { return type; }
        public String getValue() { return constant; }
        public boolean isTerminal() { return false; }
        @Override
        public String toString() { return "Constant «" + getValue() + "»"; }
    }
    
    public static class Error implements Scan {
       String error;
       public Error(String error) { this.error = error; }
       
       public ScanType getType() { return ScanType.ERROR; }
       public String getValue() { return error; }
       public boolean isTerminal() { return true; }
       @Override
       public String toString() { return "Error «" + getValue() + "»"; }
    }
    
    public static class End implements Scan {
        public End() {}
        public ScanType getType() { return ScanType.END; }
        public String getValue() { return ""; }
        public boolean isTerminal() { return true; }
        @Override
        public String toString() { return "End «»"; }
    }
}
