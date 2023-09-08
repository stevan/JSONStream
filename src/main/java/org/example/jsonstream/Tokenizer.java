package org.example.jsonstream;

import java.util.Optional;
import java.util.Stack;

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
        ARRAY,
        END_ARRAY,
        ITEM,
        END_ITEM,
        KEY_LITERAL,
        STRING_LITERAL,
        INT_LITERAL,
        FLOAT_LITERAL,
        FALSE_LITERAL,
        TRUE_LITERAL,
        NULL_LITERAL,
        ERROR
    }

    enum Context {
        IN_ROOT, IN_OBJECT, IN_ARRAY, IN_PROPERTY, IN_ITEM
    }

    State nextState;
    Stack<State> state = new Stack<>();
    Stack<Context> context = new Stack<>();

    public Tokenizer() {
        nextState = State.ROOT;
        state.push(nextState);
    }

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

            case ARRAY:
                return array(buffer);
            case END_ARRAY:
                return endArray(buffer);
            case ITEM:
                return item(buffer);
            case END_ITEM:
                return endItem(buffer);
            case KEY_LITERAL:
                return keyLiteral(buffer);
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
                // TODO: this should do something better than this
                throw new Exception();
            default:
                // TODO: This should return an ErrorToken
                throw new Exception();
        }
    }

    public Optional<Token> root(CharBuffer buffer) {
        Optional<Character> character = buffer.skipWhitespaceAndPeek();

        return character.flatMap((c) -> {
            switch (c) {
                case '{':
                    return object(buffer);
                case '[':
                    return array(buffer);
                default:
                    // TODO: this should set nextState to ERROR perhaps?
                    return Optional.of(new ErrorToken("The root node must be either an Object({}) or an Array([])"));
            }
        }).or(() -> end(buffer));
    }

    public Optional<Token> start(CharBuffer buffer) {
        return Optional.empty();
    }

    public Optional<Token> end(CharBuffer buffer) {
        // TODO: this should set nextState to END perhaps?
        return Optional.of(new NoToken());
    }

    public Optional<Token> object(CharBuffer buffer) {
        Optional<Character> character = buffer.skipWhitespaceAndPeek();

        return character.flatMap((c) -> {
            switch (c) {
                case '{':
                    buffer.skip(1);
                    state.push(State.OBJECT);
                    nextState = State.PROPERTY;
                    return Optional.of(new StartObject());
                case ',':
                    return Optional.empty();
                case '}':
                    buffer.skip(1);
                    // TODO: check if the state is not empty and peek() == OBJECT
                    state.pop(); // exit the object context
                    // TODO: check if the state is not empty here ...
                    nextState = state.pop(); // restore the previous one
                    return Optional.of(new EndObject());
                default:
                    return Optional.of(new ErrorToken("Expected end of object or start of property, but found ("+c+")"));
            }
        }).or(() -> end(buffer));
    }

    public Optional<Token> endObject(CharBuffer buffer) {
        return Optional.empty();
    }

    public Optional<Token> property(CharBuffer buffer) {
        Optional<Character> character = buffer.skipWhitespaceAndPeek();

        return character.flatMap((c) -> {
            switch (c) {
                case '"':
                    state.push(State.PROPERTY);
                    nextState = State.KEY_LITERAL;
                    return Optional.of(new StartProperty());
                case ':':
                    return Optional.empty();
                default:
                    return object(buffer);
            }
        }).or(() -> end(buffer));
    }

    public Optional<Token> keyLiteral(CharBuffer buffer) {
        Optional<Character> character = buffer.get();

        return character.flatMap((c) -> {
            if (c != '"') {
                return Optional.of(new ErrorToken("String must begin with a double-quote character"));
            }

            boolean done = false;
            StringBuilder st = new StringBuilder();
            while (!done) {
                Optional<Character> nextChar = buffer.get();
                if (nextChar.isPresent()) {
                    Character next = nextChar.get();
                    switch (next) {
                        case '"':
                            done = true;
                            break;
                        default:
                            st.append(next);
                    }
                }
                else {
                    done = true;
                }
            }

            nextState = State.PROPERTY; // go back to property to finish this ...
            return Optional.of(new AddKey(st.toString()));
        }).or(() -> end(buffer));
    }

    public Optional<Token> endProperty(CharBuffer buffer) {
        return Optional.empty();
    }

    public Optional<Token> array(CharBuffer buffer) {
        return Optional.empty();
    }

    public Optional<Token> endArray(CharBuffer buffer) {
        return Optional.empty();
    }

    public Optional<Token> item(CharBuffer buffer) {
        return Optional.empty();
    }

    public Optional<Token> endItem(CharBuffer buffer) {
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
