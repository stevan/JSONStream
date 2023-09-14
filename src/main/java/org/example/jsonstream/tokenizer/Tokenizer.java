package org.example.jsonstream.tokenizer;

import java.util.Optional;
import java.util.Stack;
import java.util.stream.Stream;

public class Tokenizer implements TokenProducer {

    public enum Context {
        IN_ROOT,
        IN_ERROR,
        IN_OBJECT, IN_PROPERTY,
        IN_ARRAY, IN_ITEM
    }

    private enum State {
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
    
    private final CharBuffer buffer;
    private final Stack<State> stack = new Stack<>();
    private final Stack<Context> context = new Stack<>();
    
    private State nextState;

    public Tokenizer(CharBuffer buff) {
        nextState = State.ROOT;
        stack.push(nextState);
        context.push(Context.IN_ROOT);
        buffer = buff;
    }
    
    public CharBuffer getBuffer() { return buffer; }

    public Boolean isInErrorState () { return nextState == State.ERROR; }
    public Boolean isInEndState () { return nextState == State.END; }
    // TODO: add some other state predicates here, as needed
    
    public boolean isDone() {
        return isInEndState() || isInErrorState();
    }
    
    public TokenIterator iterator() {
        return new TokenIterator(this);
    }
    
    public Stream<Tokens.Token> stream() {
        return Stream.iterate(
            produceToken(),
            (t) -> !t.isTerminal(), // TODO - also check isDone here perhaps
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
        
        token.setContext(context.toArray(new Context[]{}));

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
        }).orElseGet(this::end);
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
                    context.push(Context.IN_OBJECT);
                    stack.push(State.OBJECT);
                    //context.push(Context.IN_PROPERTY);
                    nextState = State.PROPERTY;
                    return new Tokens.StartObject();
                case ',':
                    if (stack.peek() == State.PROPERTY) {
                        return endProperty();
                    }
                    buffer.skip(1);
                    return property();
                case '}':
                    // XXX - check if the state is not empty
                    if (stack.peek() == State.PROPERTY) {
                        return endProperty();
                    }
                    buffer.skip(1);
                    // XXX - check if the context is not empty and peek() == IN_OBJECT
                    context.pop();
                    // XXX - check if the stack is not empty and peek() == OBJECT
                    stack.pop();
                    nextState = stack.peek(); // restore the previous one
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
                    context.push(Context.IN_PROPERTY);
                    stack.push(State.PROPERTY);
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
        // XXX - check if the context is not empty and peek() == IN_PROPERTY
        context.pop();
        // XXX - check if the stack is not empty and peek() == PROPERTY
        stack.pop(); // exit the property context
        nextState = State.OBJECT;
        return new Tokens.EndProperty();
    }

    public Tokens.Token array() {
        Optional<Character> character = buffer.skipWhitespaceAndPeek();
        
        return character.map((c) -> {
            switch (c) {
                case '[':
                    buffer.skip(1);
                    context.push(Context.IN_ARRAY);
                    stack.push(State.ARRAY);
                    nextState = State.ITEM;
                    return new Tokens.StartArray();
                case ',':
                    if (stack.peek() == State.ITEM) {
                        return endItem();
                    }
                    buffer.skip(1);
                    return item();
                case ']':
                    // XXX - check if the state is not empty
                    if (stack.peek() == State.ITEM) {
                        return endItem();
                    }
                    buffer.skip(1);
                    // XXX - check if the context is not empty and peek() == IN_ARRAY
                    context.pop();
                    // XXX - check if the stack is not empty and peek() == ARRAY
                    stack.pop();
                    nextState = stack.peek(); // restore the previous one
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
        
        // Handle the item itself
            // if we are in item context
            if (stack.peek() == State.END_ITEM) {
                Tokens.Token value = start(); // grab whatever value we find
                // XXX - check if the Token is an ErrorToken, in which case just return it
                //       although perhaps we want to set an ERROR state too?? hmmm
                // XXX - check to be sure we are back in the same item state again
                if (nextState == null) nextState = State.END_ITEM;
                return value;
            }
            
        // Handle starting the item
            // otherwise, start the item and "recurse"
            context.push(Context.IN_ITEM);
            stack.push(State.END_ITEM);
            nextState = State.ITEM;
            return new Tokens.StartItem();
        }).orElseGet(() -> error("item() expected more characters"));
    }

    public Tokens.Token endItem() {
        // XXX - check if the state is not empty and peek() == ITEM
        // XXX - check if the context is not empty and peek() == IN_ARRAY
        context.pop();
        // XXX - check if the stack is not empty and peek() == ITEM
        stack.pop(); // exit the property context
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
            
            nextState = stack.peek(); // return to caller state
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
            
            nextState = stack.peek(); // go back and finish this ...
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
                nextState = stack.peek(); // return to caller state
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
                nextState = stack.peek(); // return to caller state
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
                nextState = stack.peek(); // return to caller state
                return new Tokens.AddNull();
            } else {
                return error("Bad `null` token");
            }
        }).orElseGet(() -> error("nullLiteral() expected more characters"));
    }

    public Tokens.Token error(String msg) {
        // only the enter state once ...
        if ( nextState != State.ERROR ) {
            nextState = State.ERROR;
            context.push(Context.IN_ERROR);
        }
        return new Tokens.ErrorToken(msg);
    }
    
}

