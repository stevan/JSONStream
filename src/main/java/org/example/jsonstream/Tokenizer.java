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
    public class AddString extends Constant {
        String value;
        AddString(String s) { value = s; }
        public String getValue() { return value; }
    }
    public class AddNumber extends Constant {
        Integer ival;
        Float fval;
        AddNumber(Integer i) { ival = i; }
        AddNumber(Float f) { fval = f; }
        public boolean hasIntValue() { return ival != null; }
        public Integer getIntValue() { return ival; }
        public boolean hasFloatValue() { return fval != null; }
        public Float getFloatValue() { return fval; }
    }

    enum State {
        ROOT,
        START,
        END,
        OBJECT,
        PROPERTY,
        END_PROPERTY,
        ARRAY,
        END_ARRAY,
        ITEM,
        END_ITEM,
        STRING_LITERAL,
        NUMERIC_LITERAL,
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
        Optional<Token> token;
        
        State currState = nextState;
        nextState = null;
        
        switch (currState) {
            case ROOT:
                token = root(buffer);
                break;
            case START:
                token = start(buffer);
                break;

            case END:
                token = end(buffer);
                break;

            case OBJECT:
                token = object(buffer);
                break;

            case PROPERTY:
                token = property(buffer);
                break;
                
            case END_PROPERTY:
                token = endProperty(buffer);
                break;

            case ARRAY:
                token = array(buffer);
                break;
            case END_ARRAY:
                token = endArray(buffer);
                break;
            case ITEM:
                token = item(buffer);
                break;
            case END_ITEM:
                token = endItem(buffer);
                break;
            case STRING_LITERAL:
                token = stringLiteral(buffer);
                break;
            case NUMERIC_LITERAL:
                token = numericLiteral(buffer);
                break;
            case FALSE_LITERAL:
                token = falseLiteral(buffer);
                break;
            case TRUE_LITERAL:
                token = trueLiteral(buffer);
                break;
            case NULL_LITERAL:
                token = nullLiteral(buffer);
                break;
            case ERROR:
                // XXX - this should do something better than this
                throw new Exception();
            default:
                // XXX - This should return an ErrorToken
                token = Optional.of(new ErrorToken("Did not recognise state ("+currState+")"));
        }
        
        return token;
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
                    // XXX - this should set nextState to ERROR perhaps?
                    return Optional.of(new ErrorToken("The root node must be either an Object({}) or an Array([])"));
            }
        }).or(() -> end(buffer));
    }
    
        /*
        
        <state | f()>
            <char> -> <token>        : <nextState | stack-actions()> : <stack>
            <char> -> <f() -> token> : <nextState | stack-actions()> : <stack>
            <char> -> <f() -> token>
        
    0    ROOT | root()
           '{' -> object()
        
         OBJECT | object()
    1       '{' -> StartObject : PROPERTY : [ROOT, OBJECT]
            ',' -> property()
            '}' -> EndObject : <stack discard(PROPERTY) pop(OBJECT)> : [ROOT]
            
         PROPERTY | property()
    2       " -> StartProperty : STRING_LITERAL : [ROOT, OBJECT, PROPERTY]
    4        : -> start() : END_PROPERTY : [ROOT, OBJECT, PROPERTY]
              -> object()
    
        END_PROPERTY : endProperty()
            -> EndProperty : <stack discard(PROPERTY)>, OBJECT : [ROOT, OBJECT]
    
        STRING_LITERAL : stringLiteral()
    3        " -> AddString : <stack peek(*PROPERTY*)> : *[ROOT, OBJECT, PROPERTY]*
            
        start()
            '{' -> object()
            '[' -> array()
            '"' -> stringLiteral()
            't' -> trueLiteral()
            'f' -> falseLiteral()
            'n' -> nullLiteral()
            '-'|\d -> numericLiteral()
    
     */

    public Optional<Token> start(CharBuffer buffer) {
        Optional<Character> character = buffer.skipWhitespaceAndPeek();
        
        return character.flatMap((c) -> {
            switch (c) {
                case '{':
                    return object(buffer);
                case '[':
                    return array(buffer);
                case '"':
                    return stringLiteral(buffer);
                case 't':
                    return trueLiteral(buffer);
                case 'f':
                    return falseLiteral(buffer);
                case 'n':
                    return nullLiteral(buffer);
                case '-':
                    return numericLiteral(buffer);
                default:
                    if (Character.isDigit(c)) {
                        return numericLiteral(buffer);
                    }
                    return Optional.of(new ErrorToken("Unrecognized start character ("+c+")"));
            }
        }).or(() -> end(buffer));
    }

    public Optional<Token> end(CharBuffer buffer) {
        // XXX - this should set nextState to END perhaps?
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
                    if (state.peek() == State.PROPERTY) {
                        //System.out.println("END PROPERTY -> " + state.peek()); // exit the object context
                        return endProperty(buffer);
                    }
                    buffer.skip(1);
                    return property(buffer);
                case '}':
                    // XXX - check if the state is not empty
                    if (state.peek() == State.PROPERTY) {
                        //System.out.println("END PROPERTY -> " + state.peek()); // exit the object context
                        return endProperty(buffer);
                    }
                    buffer.skip(1);
                    // XXX - check if the state is not empty here ...
                    state.pop();
                    nextState = state.peek(); // restore the previous one
                    return Optional.of(new EndObject());
                default:
                    return Optional.of(new ErrorToken("Expected end of object or start of property, but found ("+c+")"));
            }
        }).or(() -> end(buffer));
    }

    public Optional<Token> property(CharBuffer buffer) {
        Optional<Character> character = buffer.skipWhitespaceAndPeek();

        return character.flatMap((c) -> {
            switch (c) {
                case '"':
                    state.push(State.PROPERTY);
                    nextState = State.STRING_LITERAL;
                    return Optional.of(new StartProperty());
                case ':':
                    // XXX - check to be sure we are still in property state here
                    buffer.skip(1);    // skip over the :
                    Optional<Token> value = start(buffer); // and grab whatever value we find
                    // XXX - check if the Token is an ErrorToken, in which case just return it
                    //       although perhaps we want to set an ERROR state too?? hmmm
                    // XXX - check to be sure we are back in the same property state again
                    if (nextState == null) nextState = State.END_PROPERTY;
                    return value;
                default:
                    return object(buffer);
            }
        }).or(() -> end(buffer));
    }

    public Optional<Token> endProperty(CharBuffer buffer) {
        // XXX - check if the state is not empty and peek() == PROPERTY
        //System.out.println("endProperty() -> " + state.peek());
        state.pop(); // exit the property context
        nextState = State.OBJECT;
        return Optional.of(new EndProperty());
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
        Optional<Character> character = buffer.getNext(); // grab the quote character
        
        return character.flatMap((c) -> {
            if (c != '"') {
                return Optional.of(new ErrorToken("String must begin with a double-quote character"));
            }
            
            StringBuilder acc = new StringBuilder();
            
            // XXX - this should handle escape chars
            for (Optional<Character> next = buffer.getNext(); // get the first character
                 next.isPresent() && !next.get().equals('"'); // continue if it is not empty or a quote
                 next = buffer.getNext()) {                   // grab another for the next go round
                acc.append(next.get());                       // append all the characters
            }

            nextState = state.peek(); // go back to property to finish this ...
            return Optional.of(new AddString(acc.toString()));
        }).or(() -> end(buffer));
    }

    public Optional<Token> numericLiteral(CharBuffer buffer) {
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
