package org.example.jsonstream;

import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

public class Tokenizer {

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

    State nextState;
    Stack<State> state = new Stack<>();

    public Tokenizer() {
        nextState = State.ROOT;
        state.push(nextState);
    }

    public Tokens.Token produceToken(CharBuffer buffer) {
        Tokens.Token token;
        
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
                token = error(buffer);
            default:
                // XXX - This should return an ErrorToken
                token = new Tokens.ErrorToken("Did not recognise state ("+currState+")");
        }
        
        return token;
    }

    public Tokens.Token root(CharBuffer buffer) {
        Optional<Character> character = buffer.skipWhitespaceAndPeek();

        return character.map((c) -> {
            switch (c) {
                case '{':
                    return object(buffer);
                case '[':
                    return array(buffer);
                default:
                    // XXX - this should set nextState to ERROR perhaps?
                    return new Tokens.ErrorToken("The root node must be either an Object({}) or an Array([])");
            }
        }).orElse(end(buffer));
    }

    public Tokens.Token start(CharBuffer buffer) {
        Optional<Character> character = buffer.skipWhitespaceAndPeek();
        
        return character.map((c) -> {
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
                    return new Tokens.ErrorToken("Unrecognized start character ("+c+")");
            }
        }).orElse(end(buffer));
    }

    public Tokens.Token end(CharBuffer buffer) {
        // XXX - this should set nextState to END perhaps?
        // XXX - this should make sure there is not en error
        //       meaning that the `state` stack is empty
        //       and the buffer is done.
        return new Tokens.NoToken();
    }

    public Tokens.Token object(CharBuffer buffer) {
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
                    return new Tokens.EndObject();
                default:
                    return new Tokens.ErrorToken("Expected end of object or start of property, but found ("+c+")");
            }
        }).orElse(end(buffer));
    }

    public Tokens.Token property(CharBuffer buffer) {
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
                    Tokens.Token value = start(buffer); // and grab whatever value we find
                    // XXX - check if the Token is an ErrorToken, in which case just return it
                    //       although perhaps we want to set an ERROR state too?? hmmm
                    // XXX - check to be sure we are back in the same property state again
                    if (nextState == null) nextState = State.END_PROPERTY;
                    return value;
                default:
                    return object(buffer);
            }
        }).orElse(end(buffer));
    }

    public Tokens.Token endProperty(CharBuffer buffer) {
        // XXX - check if the state is not empty and peek() == PROPERTY
        //System.out.println("endProperty() -> " + state.peek());
        state.pop(); // exit the property context
        nextState = State.OBJECT;
        return new Tokens.EndProperty();
    }

    public Tokens.Token array(CharBuffer buffer) {
        return new Tokens.StartArray();
    }

    public Tokens.Token endArray(CharBuffer buffer) {
        return new Tokens.EndArray();
    }

    public Tokens.Token item(CharBuffer buffer) {
        return new Tokens.StartItem();
    }

    public Tokens.Token endItem(CharBuffer buffer) {
        return new Tokens.EndItem();
    }
    
    public Tokens.Token stringLiteral(CharBuffer buffer) {
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
        }).orElse(end(buffer));
    }

    public Tokens.Token numericLiteral(CharBuffer buffer) {
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
        }).orElse(end(buffer));
    }

    public Tokens.Token falseLiteral(CharBuffer buffer) {
        return new Tokens.AddFalse();
    }

    public Tokens.Token trueLiteral(CharBuffer buffer) {
        return new Tokens.AddTrue();
    }

    public Tokens.Token nullLiteral(CharBuffer buffer) {
        return new Tokens.AddNull();
    }

    public Tokens.Token error(CharBuffer buffer) {
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
