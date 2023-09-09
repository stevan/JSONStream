package org.example.jsonstream;

import java.util.Optional;
import java.util.Stack;
import java.util.stream.Stream;

public class Tokenizer {

    enum State {
        ROOT,
        END,
        
        OBJECT,
        PROPERTY,
        END_PROPERTY,
        
        ARRAY,
        ITEM,
        END_ITEM,
        
        KEY_LITERAL,
        STRING_LITERAL,
        NUMERIC_LITERAL,
        FALSE_LITERAL,
        TRUE_LITERAL,
        NULL_LITERAL,
        
        ERROR
    }

    CharBuffer buffer;
    State nextState;
    Stack<State> state = new Stack<>();

    public Tokenizer(CharBuffer buff) {
        nextState = State.ROOT;
        state.push(nextState);
        buffer = buff;
    }
    
    public Stream<Tokens.Token> asStream() {
        return Stream.iterate(
            produceToken(),
            (t) -> !t.isTerminal(),
            (t) -> produceToken()
        );
    }

    public Tokens.Token produceToken() {
        Tokens.Token token;
        
        State currState = nextState;
        nextState = null;
        
        switch (currState) {
            case ROOT:
                token = root();
                break;
            case END:
                token = end();
                break;
            // Objects
            case OBJECT:
                token = object();
                break;
            case PROPERTY:
                token = property();
                break;
            case END_PROPERTY:
                token = endProperty();
                break;
            // Arrays
            case ARRAY:
                token = array();
                break;
            case ITEM:
                token = item();
                break;
            case END_ITEM:
                token = endItem();
                break;
            // Literals
            case KEY_LITERAL:
                token = keyLiteral();
                break;
            case STRING_LITERAL:
                token = stringLiteral();
                break;
            case NUMERIC_LITERAL:
                token = numericLiteral();
                break;
            case FALSE_LITERAL:
                token = falseLiteral();
                break;
            case TRUE_LITERAL:
                token = trueLiteral();
                break;
            case NULL_LITERAL:
                token = nullLiteral();
                break;
            // Errors
            case ERROR:
                // XXX - this should do something better than this
                token = error("Unknown Error");
                break;
            // in theory this can never happen, but javac complains, so *shrug*
            default:
                token = error("Did not recognise state ("+currState+")");
        }
        
        return token;
    }

    public Tokens.Token root() {
        Optional<Character> character = buffer.skipWhitespaceAndPeek();

        return character.map((c) -> {
            switch (c) {
                case '{':
                    return object();
                case '[':
                    return array();
                default:
                    // XXX - this should set nextState to ERROR perhaps?
                    return error("The root node must be either an Object({}) or an Array([])");
            }
        }).orElseGet(() -> end());
    }

    public Tokens.Token start() {
        Optional<Character> character = buffer.skipWhitespaceAndPeek();
        
        return character.map((c) -> {
            switch (c) {
                case '{':
                    return object();
                case '[':
                    return array();
                case '"':
                    return stringLiteral();
                case 't':
                    return trueLiteral();
                case 'f':
                    return falseLiteral();
                case 'n':
                    return nullLiteral();
                case '-':
                    return numericLiteral();
                default:
                    if (Character.isDigit(c)) {
                        return numericLiteral();
                    }
                    return error("Unrecognized start character ("+c+")");
            }
        }).orElseGet(() -> error("start() expected more characters"));
    }

    public Tokens.Token end() {
        // XXX - this should make sure there is not en error
        //       meaning that the `state` stack is empty
        //       and the buffer is done.
        nextState = State.END;
        return new Tokens.NoToken();
    }

    public Tokens.Token object() {
        Optional<Character> character = buffer.skipWhitespaceAndPeek();

        return character.map((c) -> {
            switch (c) {
                case '{':
                    buffer.skip(1);
                    state.push(State.OBJECT);
                    nextState = State.PROPERTY;
                    return new Tokens.StartObject();
                case ',':
                    if (state.peek() == State.PROPERTY) {
                        //System.out.println("END PROPERTY -> " + state.peek()); // exit the object context
                        return endProperty();
                    }
                    buffer.skip(1);
                    return property();
                case '}':
                    // XXX - check if the state is not empty
                    if (state.peek() == State.PROPERTY) {
                        //System.out.println("END PROPERTY -> " + state.peek()); // exit the object context
                        return endProperty();
                    }
                    buffer.skip(1);
                    // XXX - check if the state is not empty here ...
                    state.pop();
                    nextState = state.peek(); // restore the previous one
                    return new Tokens.EndObject();
                default:
                    return error("Expected end of object or start of property, but found ("+c+")");
            }
        }).orElseGet(() -> error("object() expected more characters"));
    }

    public Tokens.Token property() {
        Optional<Character> character = buffer.skipWhitespaceAndPeek();

        return character.map((c) -> {
            switch (c) {
                case '"':
                    state.push(State.PROPERTY);
                    nextState = State.KEY_LITERAL;
                    return new Tokens.StartProperty();
                case ':':
                    // XXX - check to be sure we are still in property state here
                    buffer.skip(1);    // skip over the :
                    Tokens.Token value = start(); // and grab whatever value we find
                    // XXX - check if the Token is an ErrorToken, in which case just return it
                    //       although perhaps we want to set an ERROR state too?? hmmm
                    // XXX - check to be sure we are back in the same property state again
                    if (nextState == null) nextState = State.END_PROPERTY;
                    return value;
                default:
                    return object();
            }
        }).orElseGet(() -> error("property() expected more characters"));
    }

    public Tokens.Token endProperty() {
        // XXX - check if the state is not empty and peek() == PROPERTY
        //System.out.println("endProperty() -> " + state.peek());
        state.pop(); // exit the property context
        nextState = State.OBJECT;
        return new Tokens.EndProperty();
    }

    public Tokens.Token array() {
        Optional<Character> character = buffer.skipWhitespaceAndPeek();
        
        return character.map((c) -> {
            //System.out.println("-- Entering StartArray -------------------------------------");
            switch (c) {
                case '[':
                    buffer.skip(1);
                    state.push(State.ARRAY);
                    nextState = State.ITEM;
                    return new Tokens.StartArray();
                case ',':
                    if (state.peek() == State.ITEM) {
                        //System.out.println("array() -> END ITEM -> " + state.peek());
                        return endItem();
                    }
                    buffer.skip(1);
                    return item();
                case ']':
                    // XXX - check if the state is not empty
                    if (state.peek() == State.ITEM) {
                        //System.out.println("END ITEM -> " + state.peek());
                        return endItem();
                    }
                    buffer.skip(1);
                    // XXX - check if the state is not empty here ...
                    state.pop();
                    nextState = state.peek(); // restore the previous one
                    return new Tokens.EndArray();
                default:
                    return error("Expected end of array or start of an item, but found ("+c+")");
            }
        }).orElseGet(() -> error("array() expected more characters"));
    }

    public Tokens.Token item() {
        Optional<Character> character = buffer.skipWhitespaceAndPeek();
        
        return character.map((c) -> {
            if ( c == ']' ) {
                return array();
            }
            
            // if we are in item context
            //System.out.println("-- Entering StartItem -------------------------------------");
            if (state.peek() == State.END_ITEM) {
                Tokens.Token value = start(); // grab whatever value we find
                // XXX - check if the Token is an ErrorToken, in which case just return it
                //       although perhaps we want to set an ERROR state too?? hmmm
                // XXX - check to be sure we are back in the same item state again
                //System.out.println("(before) IN ITEM: nextState " + (nextState == null ? "NULL" : nextState));
                if (nextState == null) nextState = State.END_ITEM;
                //System.out.println("IN ITEM: nextState " + nextState);
                //System.out.println("IN ITEM: token " + value);
                return value;
            }
            
            // otherwise, start the item and "recurse"
            //System.out.println("StartItem and RECURSE");
            state.push(State.END_ITEM);
            nextState = State.ITEM;
            return new Tokens.StartItem();
        }).orElseGet(() -> error("item() expected more characters"));
    }

    public Tokens.Token endItem() {
        // XXX - check if the state is not empty and peek() == ITEM
        //System.out.println("endItem() -> " + state.peek());
        state.pop(); // exit the property context
        nextState = State.ARRAY;
        return new Tokens.EndItem();
    }
    
    public Tokens.Token keyLiteral() {
        Optional<Character> character = buffer.getNext(); // grab the quote character
        
        return character.map((c) -> {
            if (c != '"') {
                return error("String must begin with a double-quote character");
            }
            
            String str = buffer.asStream()
                             .takeWhile((n) -> n != '"')
                             .map(String::valueOf)
                             .reduce("", (a, n) -> a + n );
            
            nextState = State.PROPERTY; // return to caller state
            return new Tokens.AddKey(str);
        }).orElseGet(() -> error("keyLiteral() expected more characters"));
    }
    
    public Tokens.Token stringLiteral() {
        Optional<Character> character = buffer.getNext(); // grab the quote character
        
        return character.map((c) -> {
            if (c != '"') {
                return error("String must begin with a double-quote character");
            }
            
            String str = buffer.asStream()
                             .takeWhile((n) -> n != '"')
                             .map(String::valueOf)
                             .reduce("", (a, n) -> a + n );
            
            nextState = state.peek(); // return to caller state
            return new Tokens.AddString(str);
        }).orElseGet(() -> error("stringLiteral() expected more characters"));
    }

    public Tokens.Token numericLiteral() {
        Optional<Character> character = buffer.getNext();

        return character.map((c) -> {
            if (c != '-' && !Character.isDigit(c)) {
                return error("Number must start with a sign or digit");
            }
            
            String num = buffer.streamWhile((n) -> n == '.' || Character.isDigit(n))
                             .map(String::valueOf)
                             .reduce(String.valueOf(c), (a, n) -> a + n );
            
            nextState = state.peek(); // go back and finish this ...
            if ( num.contains(".") ) {
                return new Tokens.AddFloat(Float.parseFloat(num));
            }
            else {
                return new Tokens.AddInt(Integer.parseInt(num));
            }
        }).orElseGet(() -> error("numericLiteral() expected more characters"));
    }
    
    private boolean matchLiteral(String expected) {
        StringBuilder acc = new StringBuilder();
        
        expected.chars().mapToObj((c) -> (char) c).forEach((c) -> {
            Optional<Character> peek = buffer.peek().filter((n) -> n == c);
            if ( peek.isPresent() ) {
                acc.append( peek.get() );
                buffer.skip(1);
            }
        });

        return acc.toString().equals(expected);
    }

    public Tokens.Token falseLiteral() {
        Optional<Character> character = buffer.getNext();
        
        return character.map((c) -> {
            if (c != 'f') {
                return error("False literal must start with `f`");
            }
            
            if (matchLiteral("alse")) {
                nextState = state.peek(); // return to caller state
                return new Tokens.AddFalse();
            } else {
                return error("Bad `false` token");
            }
        }).orElseGet(() -> error("falseLiteral() expected more characters"));
    }

    public Tokens.Token trueLiteral() {
        Optional<Character> character = buffer.getNext();
        
        return character.map((c) -> {
            if (c != 't') {
                return error("False literal must start with `f`");
            }
            
            if (matchLiteral("rue")) {
                nextState = state.peek(); // return to caller state
                return new Tokens.AddTrue();
            } else {
                return error("Bad `true` token");
            }
        }).orElseGet(() -> error("trueLiteral() expected more characters"));
    }

    public Tokens.Token nullLiteral() {
        Optional<Character> character = buffer.getNext();
        
        return character.map((c) -> {
            if (c != 'n') {
                return error("False literal must start with `f`");
            }
            
            if (matchLiteral("ull")) {
                nextState = state.peek(); // return to caller state
                return new Tokens.AddNull();
            } else {
                return error("Bad `null` token");
            }
        }).orElseGet(() -> error("nullLiteral() expected more characters"));
    }

    public Tokens.Token error(String msg) {
        nextState = State.ERROR;
        return new Tokens.ErrorToken(msg);
    }
    
}

