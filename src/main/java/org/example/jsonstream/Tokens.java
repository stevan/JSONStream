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
    
    public interface Token {}

    public static class NoToken implements Token {
        public static final Type type = Type.NO_TOKEN;
    }
    public static class ErrorToken implements Token {
        public static final Type type = Type.ERROR_TOKEN;
        String msg;
        ErrorToken(String m) { msg = m; }
        public String getMsg() { return msg; }
        
        @Override
        public String toString() { return "Error[" + msg + "]"; }
    }

    public static class StartObject implements Token {
        public static final Type type = Type.START_OBJECT;
    }
    public static class EndObject implements Token {
        public static final Type type = Type.END_OBJECT;
    }

    public static class StartProperty implements Token {
        public static final Type type = Type.START_PROPERTY;
    }
    public static class EndProperty implements Token {
        public static final Type type = Type.END_PROPERTY;
    }

    public static class StartArray implements Token {
        public static final Type type = Type.START_ARRAY;
    }
    public static class EndArray implements Token {
        public static final Type type = Type.END_ARRAY;
    }

    public static class StartItem implements Token {
        public static final Type type = Type.START_ITEM;
    }
    public static class EndItem implements Token {
        public static final Type type = Type.END_ITEM;
    }

    public static class AddTrue implements Token {
        public static final Type type = Type.ADD_TRUE;
    }
    public static class AddFalse implements Token {
        public static final Type type = Type.ADD_FALSE;
    }
    public static class AddNull implements Token {
        public static final Type type = Type.ADD_NULL;
    }
    
    public static class AddKey implements Token {
        public static final Type type = Type.ADD_KEY;
        String value;
        AddKey(String s) { value = s; }
        public String getValue() { return value; }
    }
    
    public static class AddString implements Token {
        public static final Type type = Type.ADD_STRING;
        String value;
        AddString(String s) { value = s; }
        public String getValue() { return value; }
    }
    
    public static class AddInt implements Token {
        public static final Type type = Type.ADD_INT;
        Integer value;
        AddInt(Integer i) { value = i; }
        public Integer getValue() { return value; }
    }
    
    public static class AddFloat implements Token {
        public static final Type type = Type.ADD_FLOAT;
        Float value;
        AddFloat(Float f) { value = f; }
        public Float getValue() { return value; }
    }
    
}
