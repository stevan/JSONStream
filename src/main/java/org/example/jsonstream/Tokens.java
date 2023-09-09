package org.example.jsonstream;

public class Tokens {
    public enum Type {
        NO_TOKEN,
        ERROR_TOKEN,
        
        START_OBJECT, END_OBJECT,
        START_PROPERTY, END_PROPERTY, ADD_KEY,
        
        START_ARRAY, END_ARRAY,
        START_ITEM, END_ITEM,
        
        ADD_TRUE, ADD_FALSE, ADD_NULL,
        
        ADD_STRING, ADD_INT, ADD_FLOAT
    }
    
    public interface Token {
        Type getType();
        
        boolean isTerminal();
    }

    public static class NoToken implements Token {
        public Type getType() { return Type.NO_TOKEN; }
        public boolean isTerminal() { return true; }
    }

    public static class ErrorToken implements Token {
        String msg;

        ErrorToken(String m) { msg = m; }

        public String getMsg() { return msg; }
        public Type getType() { return Type.ERROR_TOKEN; }
        public boolean isTerminal() { return true; }
        
        @Override
        public String toString() { return "Error[" + msg + "]"; }
    }

    public static class StartObject implements Token {
        public Type getType() { return Type.START_OBJECT; }
        public boolean isTerminal() { return false; }
    }

    public static class EndObject implements Token {
        public Type getType() { return Type.END_OBJECT; }
        public boolean isTerminal() { return false; }
    }

    public static class StartProperty implements Token {
        public Type getType() { return Type.START_PROPERTY; }
        public boolean isTerminal() { return false; }
    }

    public static class EndProperty implements Token {
        public Type getType() { return Type.END_PROPERTY; }
        public boolean isTerminal() { return false; }
    }

    public static class StartArray implements Token {
        public Type getType() { return Type.START_ARRAY; }
        public boolean isTerminal() { return false; }
    }

    public static class EndArray implements Token {
        public Type getType() { return Type.END_ARRAY; }
        public boolean isTerminal() { return false; }
    }

    public static class StartItem implements Token {
        public Type getType() { return Type.START_ITEM; }
        public boolean isTerminal() { return false; }
    }

    public static class EndItem implements Token {
        public Type getType() { return Type.END_ITEM; }
        public boolean isTerminal() { return false; }
    }

    public static class AddTrue implements Token {
        public Type getType() { return Type.ADD_TRUE; }
        public boolean isTerminal() { return false; }
    }

    public static class AddFalse implements Token {
        public Type getType() { return Type.ADD_FALSE; }
        public boolean isTerminal() { return false; }
    }

    public static class AddNull implements Token {
        public Type getType() { return Type.ADD_NULL; }
        public boolean isTerminal() { return false; }
    }
    
    public static class AddKey implements Token {
        String value;

        AddKey(String s) { value = s; }

        public String getValue() { return value; }
        public Type getType() { return Type.ADD_KEY; }
        public boolean isTerminal() { return false; }
        
        @Override
        public String toString() { return "AddKey[" + value + "]"; }
    }
    
    public static class AddString implements Token {
        String value;

        AddString(String s) { value = s; }

        public String getValue() { return value; }
        public Type getType() { return Type.ADD_STRING; }
        public boolean isTerminal() { return false; }
        
        @Override
        public String toString() { return "AddString[" + value + "]"; }
    }
    
    public static class AddInt implements Token {
        Integer value;

        AddInt(Integer i) { value = i; }

        public Integer getValue() { return value; }
        public Type getType() { return Type.ADD_INT; }
        public boolean isTerminal() { return false; }
        
        @Override
        public String toString() { return "AddInt[" + value + "]"; }
    }
    
    public static class AddFloat implements Token {
        Float value;

        AddFloat(Float f) { value = f; }

        public Float getValue() { return value; }
        public Type getType() { return Type.ADD_FLOAT; }
        public boolean isTerminal() { return false; }
        
        @Override
        public String toString() { return "AddFloat[" + value + "]"; }
    }
    
}
