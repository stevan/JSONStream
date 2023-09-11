package org.example.jsonstream.tokenizer;

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
        
        Tokenizer.Context[] getContext();
        void setContext(Tokenizer.Context[] ctx);

        Integer getContextDepth();
        Tokenizer.Context getCurrentContext();

        boolean isTerminal();
    }
    
    private static abstract class BaseToken implements Token {
        Tokenizer.Context[] context;

        public Tokenizer.Context[] getContext() {
            return context;
        }
        public void setContext(Tokenizer.Context[] ctx) {
            context = ctx;
        }
        public Integer getContextDepth() { return context.length; }
        public Tokenizer.Context getCurrentContext() {
            return context[context.length - 1];
        }

        public boolean isTerminal() { return false; }
        
        @Override
        public String toString() { return this.getClass().getSimpleName(); }
    }
    
    // Terminals

    public static class NoToken extends BaseToken {
        public Type getType() { return Type.NO_TOKEN; }
        
        @Override
        public boolean isTerminal() { return true; }
    }
    
    public static class ErrorToken extends BaseToken {
        final String msg;
        
        public ErrorToken(String m) { msg = m; }

        public String getMsg() { return msg; }
        public Type getType() { return Type.ERROR_TOKEN; }
        
        @Override
        public boolean isTerminal() { return true; }
        
        @Override
        public String toString() { return "Error[" + msg + "]"; }
    }
    
    // Non Terminals

    public static class StartObject extends BaseToken {
        public Type getType() { return Type.START_OBJECT; }
    }

    public static class EndObject extends BaseToken {
        public Type getType() { return Type.END_OBJECT; }
    }

    public static class StartProperty extends BaseToken {
        public Type getType() { return Type.START_PROPERTY; }
    }

    public static class EndProperty extends BaseToken {
        public Type getType() { return Type.END_PROPERTY; }
    }

    public static class StartArray extends BaseToken {
        public Type getType() { return Type.START_ARRAY; }
    }

    public static class EndArray extends BaseToken {
        public Type getType() { return Type.END_ARRAY; }
    }

    public static class StartItem extends BaseToken {
        public Type getType() { return Type.START_ITEM; }
    }

    public static class EndItem extends BaseToken {
        public Type getType() { return Type.END_ITEM; }
    }

    public static class AddTrue extends BaseToken {
        public Type getType() { return Type.ADD_TRUE; }
    }

    public static class AddFalse extends BaseToken {
        public Type getType() { return Type.ADD_FALSE; }
    }

    public static class AddNull extends BaseToken {
        public Type getType() { return Type.ADD_NULL; }
    }
    
    public static class AddKey extends BaseToken {
        final String value;

        public AddKey(String s) { value = s; }

        public String getValue() { return value; }
        public Type getType() { return Type.ADD_KEY; }
        
        @Override
        public String toString() { return "AddKey[" + value + "]"; }
    }
    
    public static class AddString extends BaseToken {
        final String value;
        
        public AddString(String s) { value = s; }

        public String getValue() { return value; }
        public Type getType() { return Type.ADD_STRING; }
        
        @Override
        public String toString() { return "AddString[" + value + "]"; }
    }
    
    public static class AddInt extends BaseToken {
        final Integer value;
        
        public AddInt(Integer i) { value = i; }

        public Integer getValue() { return value; }
        public Type getType() { return Type.ADD_INT; }
        
        @Override
        public String toString() { return "AddInt[" + value + "]"; }
    }
    
    public static class AddFloat extends BaseToken {
        final Float value;
        
        public AddFloat(Float f) { value = f; }

        public Float getValue() { return value; }
        public Type getType() { return Type.ADD_FLOAT; }
        
        @Override
        public String toString() { return "AddFloat[" + value + "]"; }
    }
    
}
