package org.example.jsonstream;

import java.util.Optional;

public class Tokenizer {

    public static class Token {}

    public static class NoToken extends Token {}
    public static class ErrorToken extends Token {
        String msg;
        ErrorToken(String m) { msg = m; }
        public String getMsg() { return msg; }
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
    public class AddKey extends Constant {
        String key;
        AddKey(String k) { key = k; }
        public String getValue() { return key; }
    }
    public class AddString extends Constant {
        String value;
        AddString(String s) { value = s; }
        public String getValue() { return value; }
    }
    public class AddInt extends Constant {
        Integer value;
        AddInt(Integer i) { value = i; }
        public Integer getValue() { return value; }
    }
    public class AddFloat extends Constant {
        Float value;
        AddFloat(Float f) { value = f; }
        public Float getValue() { return value; }
    }

    enum State {
        ROOT,
        START,
        END,
        OBJECT,
        END_OBJECT,
        PROPERTY,
        END_PROPERTY,
        STRING_LITERAL,
        INT_LITERAL,
        FLOAT_LITERAL,
        FALSE_LITERAL,
        TRUE_LITERAL,
        NULL_LITERAL,
        ERROR
    }

    State nextState = State.ROOT;

    public Optional<Token> produceToken(CharBuffer buffer) throws Exception {
        switch (nextState) {
            case ROOT:
                return root(buffer);
            case START:
                return start(buffer);
            case END:
                return end(buffer);
            case OBJECT:
                return object(buffer);
            case END_OBJECT:
                return endObject(buffer);
            case PROPERTY:
                return property(buffer);
            case END_PROPERTY:
                return endProperty(buffer);
            case STRING_LITERAL:
                return stringLiteral(buffer);
            case INT_LITERAL:
                return intLiteral(buffer);
            case FLOAT_LITERAL:
                return floatLiteral(buffer);
            case FALSE_LITERAL:
                return falseLiteral(buffer);
            case TRUE_LITERAL:
                return trueLiteral(buffer);
            case NULL_LITERAL:
                return nullLiteral(buffer);
            case ERROR:
                throw new Exception();
            default:
                throw new Exception();
        }
    }

    public Optional<Token> root(CharBuffer buffer) {
        Optional<Character> character = buffer.skipWhitespaceAndPeek();

        return character.flatMap((c) -> {
            if (c == '{' || c == '[') {
                return object(buffer);
            }
            else {
                return Optional.of(new ErrorToken("The root node must be either an Object({}) or an Array([])"));
            }
        }).or(() -> end(buffer));
    }

    public Optional<Token> start(CharBuffer buffer) {
        return Optional.empty();
    }

    public Optional<Token> end(CharBuffer buffer) {
        return Optional.of(new NoToken());
    }

    public Optional<Token> object(CharBuffer buffer) {
        return Optional.empty();
    }

    public Optional<Token> endObject(CharBuffer buffer) {
        return Optional.empty();
    }

    public Optional<Token> property(CharBuffer buffer) {
        return Optional.empty();
    }

    public Optional<Token> endProperty(CharBuffer buffer) {
        return Optional.empty();
    }

    public Optional<Token> stringLiteral(CharBuffer buffer) {
        return Optional.empty();
    }

    public Optional<Token> intLiteral(CharBuffer buffer) {
        return Optional.empty();
    }

    public Optional<Token> floatLiteral(CharBuffer buffer) {
        return Optional.empty();
    }

    public Optional<Token> falseLiteral(CharBuffer buffer) {
        return Optional.empty();
    }

    public Optional<Token> trueLiteral(CharBuffer buffer) {
        return Optional.empty();
    }

    public Optional<Token> nullLiteral(CharBuffer buffer) {
        return Optional.empty();
    }

    public Optional<Token> error(CharBuffer buffer) {
        return Optional.empty();
    }


}
