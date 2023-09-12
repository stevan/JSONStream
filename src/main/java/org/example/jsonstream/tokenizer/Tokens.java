package org.example.jsonstream.tokenizer;

import javax.swing.plaf.basic.BasicCheckBoxUI;

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
    
    private static abstract class BasicToken implements Token {
        private String name = name = this.getClass().getSimpleName();
        
        private Tokenizer.Context[] context;

        public String getName() { return name; }
        
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
        public String toString() { return name; }
    }
    
    // Terminals

    public static class NoToken extends BasicToken {
        public Type getType() { return Type.NO_TOKEN; }
        
        @Override
        public boolean isTerminal() { return true; }
    }
    
    public static class ErrorToken extends BasicToken {
        final String msg;
        
        public ErrorToken(String m) { msg = m; }

        public String getMsg() { return msg; }
        public Type getType() { return Type.ERROR_TOKEN; }
        
        @Override
        public boolean isTerminal() { return true; }
        
        @Override
        public String toString() { return getName() + "[" + msg + "]"; }
    }
    
    // Non Terminals

    public static class StartObject extends BasicToken {
        public Type getType() { return Type.START_OBJECT; }
    }

    public static class EndObject extends BasicToken {
        public Type getType() { return Type.END_OBJECT; }
    }

    public static class StartProperty extends BasicToken {
        public Type getType() { return Type.START_PROPERTY; }
    }

    public static class EndProperty extends BasicToken {
        public Type getType() { return Type.END_PROPERTY; }
    }

    public static class StartArray extends BasicToken {
        public Type getType() { return Type.START_ARRAY; }
    }

    public static class EndArray extends BasicToken {
        public Type getType() { return Type.END_ARRAY; }
    }

    public static class StartItem extends BasicToken {
        public Type getType() { return Type.START_ITEM; }
    }

    public static class EndItem extends BasicToken {
        public Type getType() { return Type.END_ITEM; }
    }

    public static class AddTrue extends BasicToken {
        public Type getType() { return Type.ADD_TRUE; }
    }

    public static class AddFalse extends BasicToken {
        public Type getType() { return Type.ADD_FALSE; }
    }

    public static class AddNull extends BasicToken {
        public Type getType() { return Type.ADD_NULL; }
    }
    
    public static class AddKey extends BasicToken {
        final String value;

        public AddKey(String s) { value = s; }

        public String getValue() { return value; }
        public Type getType() { return Type.ADD_KEY; }
        
        @Override
        public String toString() { return getName() + "[" + value + "]"; }
    }
    
    public static class AddString extends BasicToken {
        final String value;
        
        public AddString(String s) { value = s; }

        public String getValue() { return value; }
        public Type getType() { return Type.ADD_STRING; }
        
        @Override
        public String toString() { return getName() + "[" + value + "]"; }
    }
    
    public static class AddInt extends BasicToken {
        final Integer value;
        
        public AddInt(Integer i) { value = i; }

        public Integer getValue() { return value; }
        public Type getType() { return Type.ADD_INT; }
        
        @Override
        public String toString() { return getName() + "[" + value + "]"; }
    }
    
    public static class AddFloat extends BasicToken {
        final Float value;
        
        public AddFloat(Float f) { value = f; }

        public Float getValue() { return value; }
        public Type getType() { return Type.ADD_FLOAT; }
        
        @Override
        public String toString() { return getName() + "[" + value + "]"; }
    }
    
}
