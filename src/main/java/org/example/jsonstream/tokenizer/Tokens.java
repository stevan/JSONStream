package org.example.jsonstream.tokenizer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    private @interface TokenType {
        Type type();
        boolean isTerminal() default false;
    }

    public interface Token {
        Type getType();
        String getName();
        boolean isTerminal();
        
        Tokenizer.Context[] getContext();
        void setContext(Tokenizer.Context[] ctx);

        int getContextDepth();
        Tokenizer.Context getCurrentContext();
    }
    
    private static abstract class BasicToken implements Token {
        private Tokenizer.Context[] context = new Tokenizer.Context[0];

        private final String name = this.getClass().getSimpleName();
        private final Type type = this.getClass().getAnnotation(TokenType.class).type();
        private final Boolean isTerminal = this.getClass().getAnnotation(TokenType.class).isTerminal();

        public String getName() { return name; }
        public Type getType() { return type; }
        
        public boolean isTerminal() { return isTerminal; }
        
        public Tokenizer.Context[] getContext() {
            return context;
        }
        public void setContext(Tokenizer.Context[] ctx) {
            context = ctx;
        }
        public int getContextDepth() {
            return context.length;
        }
        public Tokenizer.Context getCurrentContext() {
            return context[context.length - 1];
        }
        
        @Override
        public String toString() { return name; }
    }
    
    // Terminals

    @TokenType(type = Type.NO_TOKEN, isTerminal = true)
    public static class NoToken extends BasicToken {}
    
    @TokenType(type = Type.ERROR_TOKEN, isTerminal = true)
    public static class ErrorToken extends BasicToken {
        private final String msg;
        
        public ErrorToken(String m) { msg = m; }
        public String getMsg() { return msg; }
        
        @Override
        public String toString() { return getName() + "[" + msg + "]"; }
    }
    
    // Non-Terminals

    @TokenType(type = Type.START_OBJECT)
    public static class StartObject extends BasicToken {}

    @TokenType(type = Type.END_OBJECT)
    public static class EndObject extends BasicToken {}

    @TokenType(type = Type.START_PROPERTY)
    public static class StartProperty extends BasicToken {}

    @TokenType(type = Type.END_PROPERTY)
    public static class EndProperty extends BasicToken {}

    @TokenType(type = Type.START_ARRAY)
    public static class StartArray extends BasicToken {}

    @TokenType(type = Type.END_ARRAY)
    public static class EndArray extends BasicToken {}

    @TokenType(type = Type.START_ITEM)
    public static class StartItem extends BasicToken {}

    @TokenType(type = Type.END_ITEM)
    public static class EndItem extends BasicToken {}

    @TokenType(type = Type.ADD_TRUE)
    public static class AddTrue extends BasicToken {}

    @TokenType(type = Type.ADD_FALSE)
    public static class AddFalse extends BasicToken {}

    @TokenType(type = Type.ADD_NULL)
    public static class AddNull extends BasicToken {}
    
    @TokenType(type = Type.ADD_KEY)
    public static class AddKey extends BasicToken {
        private final String value;

        public AddKey(String s) { value = s; }
        public String getValue() { return value; }
        
        @Override
        public String toString() { return getName() + "[" + value + "]"; }
    }
    
    @TokenType(type = Type.ADD_STRING)
    public static class AddString extends BasicToken {
        private final String value;
        
        public AddString(String s) { value = s; }
        public String getValue() { return value; }
        
        @Override
        public String toString() { return getName() + "[" + value + "]"; }
    }
    
    @TokenType(type = Type.ADD_INT)
    public static class AddInt extends BasicToken {
        private final Integer value;
        
        public AddInt(Integer i) { value = i; }
        public Integer getValue() { return value; }
        
        @Override
        public String toString() { return getName() + "[" + value + "]"; }
    }
    
    @TokenType(type = Type.ADD_FLOAT)
    public static class AddFloat extends BasicToken {
        private final Float value;
        
        public AddFloat(Float f) { value = f; }
        public Float getValue() { return value; }
        
        @Override
        public String toString() { return getName() + "[" + value + "]"; }
    }
    
}
