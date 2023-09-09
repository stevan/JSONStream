package org.example.jsonstream;

public class Tokens {
    public static class Token {}

    public static class NoToken extends Token {}
    public static class ErrorToken extends Token {
        String msg;
        ErrorToken(String m) { msg = m; }
        public String getMsg() { return msg; }
        
        @Override
        public String toString() { return "Error[" + msg + "]"; }
    }

    public static class StartObject extends Token {}
    public static class EndObject extends Token {}

    public static class StartProperty extends Token {}
    public static class EndProperty extends Token {}

    public static class StartArray extends Token {}
    public static class EndArray extends Token {}

    public static class StartItem extends Token {}
    public static class EndItem extends Token {}

    public static class AddTrue extends Token {}
    public static class AddFalse extends Token {}
    public static class AddNull extends Token {}

    public static class Constant extends Token {}
    
    public static class AddString extends Constant {
        String value;
        AddString(String s) { value = s; }
        public String getValue() { return value; }
    }
    
    public static class AddInt extends Constant {
        Integer value;
        AddInt(Integer i) { value = i; }
        public Integer getValue() { return value; }
    }
    
    public static class AddFloat extends Constant {
        Float value;
        AddFloat(Float f) { value = f; }
        public Float getValue() { return value; }
    }

}
