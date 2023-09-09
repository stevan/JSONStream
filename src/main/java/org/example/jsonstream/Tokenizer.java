package org.example.jsonstream;

import java.util.Optional;
import java.util.Stack;

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
                token = error();
                break;
            // in theory this can never happen, but javac complains, so *shrug*
            default:
                token = new Tokens.ErrorToken("Did not recognise state ("+currState+")");
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
                    return new Tokens.ErrorToken("The root node must be either an Object({}) or an Array([])");
            }
        }).orElse(end());
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
                    return new Tokens.ErrorToken("Unrecognized start character ("+c+")");
            }
        }).orElse(end());
    }

    public Tokens.Token end() {
        // XXX - this should set nextState to END perhaps?
        // XXX - this should make sure there is not en error
        //       meaning that the `state` stack is empty
        //       and the buffer is done.
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
                    return new Tokens.ErrorToken("Expected end of object or start of property, but found ("+c+")");
            }
        }).orElse(end());
    }

    public Tokens.Token property() {
        Optional<Character> character = buffer.skipWhitespaceAndPeek();

        return character.map((c) -> {
            switch (c) {
                case '"':
                    state.push(State.PROPERTY);
                    nextState = State.STRING_LITERAL;
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
        }).orElse(end());
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
                    return new Tokens.ErrorToken("Expected end of array or start of an item, but found ("+c+")");
            }
        }).orElse(end());
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
        }).orElse(end());
    }

    public Tokens.Token endItem() {
        // XXX - check if the state is not empty and peek() == ITEM
        //System.out.println("endItem() -> " + state.peek());
        state.pop(); // exit the property context
        nextState = State.ARRAY;
        return new Tokens.EndItem();
    }
    
    public Tokens.Token stringLiteral() {
        Optional<Character> character = buffer.getNext(); // grab the quote character
        
        return character.map((c) -> {
            if (c != '"') {
                return new Tokens.ErrorToken("String must begin with a double-quote character");
            }
            
            //System.out.println("Start String Stream");
            String str = buffer.asStream()
                             .takeWhile((n) -> n != '"')
                             .map(String::valueOf)
                             .reduce("", (a, n) -> a + n );
            
            //System.out.println("STRSTR: " + str);
            //System.out.println("BUFFER: " + buffer.toString());
            
            nextState = state.peek(); // go back to property to finish this ...
            return new Tokens.AddString(str);
        }).orElse(end());
    }

    public Tokens.Token numericLiteral() {
        Optional<Character> character = buffer.getNext();

        return character.map((c) -> {
            if (c != '-' && !Character.isDigit(c)) {
                return new Tokens.ErrorToken("Number must start with a sign or digit");
            }
            
            //System.out.println("Start Numeric Stream");
            String num = buffer.streamWhile((n) -> n == '.' || Character.isDigit(n))
                             .map(String::valueOf)
                             .reduce(String.valueOf(c), (a, n) -> a + n );
            
            //System.out.println("NUMSTR: " + num);
            //System.out.println("BUFFER: " + buffer.toString());
            nextState = state.peek(); // go back to property to finish this ...
            if ( num.contains(".") ) {
                return new Tokens.AddFloat(Float.parseFloat(num));
            }
            else {
                return new Tokens.AddInt(Integer.parseInt(num));
            }
        }).orElse(end());
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
                return new Tokens.ErrorToken("False literal must start with `f`");
            }
            
            if (matchLiteral("alse")) {
                nextState = state.peek(); // go back to property to finish this ...
                return new Tokens.AddFalse();
            } else {
                return new Tokens.ErrorToken("Bad `false` token");
            }
        }).orElse(end());
    }

    public Tokens.Token trueLiteral() {
        Optional<Character> character = buffer.getNext();
        
        return character.map((c) -> {
            if (c != 't') {
                return new Tokens.ErrorToken("False literal must start with `f`");
            }
            
            if (matchLiteral("rue")) {
                nextState = state.peek(); // go back to property to finish this ...
                return new Tokens.AddTrue();
            } else {
                return new Tokens.ErrorToken("Bad `true` token");
            }
        }).orElse(end());
    }

    public Tokens.Token nullLiteral() {
        Optional<Character> character = buffer.getNext();
        
        return character.map((c) -> {
            if (c != 'n') {
                return new Tokens.ErrorToken("False literal must start with `f`");
            }
            
            if (matchLiteral("ull")) {
                nextState = state.peek(); // go back to property to finish this ...
                return new Tokens.AddNull();
            } else {
                return new Tokens.ErrorToken("Bad `null` token");
            }
        }).orElse(end());
    }

    public Tokens.Token error() {
        return new Tokens.ErrorToken("Unknown Error");
    }


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
