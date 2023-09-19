package org.example.jsonstream.tokenizer;

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
    
    private final Scanner scanner;
    private final Stack<State> stack = new Stack<>();
    private final Stack<Context> context = new Stack<>();
    
    private State nextState;
    
    public Tokenizer(Scanner s) {
        nextState = State.ROOT;
        stack.push(nextState);
        context.push(Context.IN_ROOT);
        scanner = s;
    }
    
    public Scanner getScanner() { return scanner; }

    public boolean isInErrorState () { return nextState == State.ERROR; }
    public boolean isInEndState () { return nextState == State.END; }
    
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
        
        State currState = nextState;
        nextState = null;
        
        Tokens.Token token = switch (currState) {
            case ROOT -> root();
            case END  -> end();
            // Objects
            case OBJECT       -> object();
            case PROPERTY     -> property();
            case END_PROPERTY -> endProperty();
            // Arrays
            case ARRAY    -> array();
            case ITEM     -> item();
            case END_ITEM -> endItem();
            // Literals
            case KEY_LITERAL     -> keyLiteral();
            case STRING_LITERAL  -> stringLiteral();
            case NUMERIC_LITERAL -> numericLiteral();
            case FALSE_LITERAL   -> falseLiteral();
            case TRUE_LITERAL    -> trueLiteral();
            case NULL_LITERAL    -> nullLiteral();
            // Errors
            case ERROR ->
                // TODO - this should do something better than this
                error("Unknown Error");
        };
        
        token.setContext(captureContext());

        return token;
    }
    
    private Context[] captureContext() { return context.toArray(new Context[]{}); }

    public Tokens.Token root() {
       Scans.Scan scan = scanner.peekNextScan();
       
       if (scan.isEnd()) return end();
       if (scan.isError()) return error("Got error from scanner: "+ scan.getValue());
       
       return switch (scan.getValue()) {
           case "{" -> object();
           case "[" -> array();
           default  -> error("The root node must be either an Object({}) or an Array([])");
       };
    }

    public Tokens.Token start() {
        Scans.Scan scan = scanner.peekNextScan();
        
        if (scan.isEnd()) return error("Expected a value, got End");
        if (scan.isError()) return error("Got error from scanner: "+ scan.getValue());
        
        String value = scan.getValue();
        return switch (value) {
            case "{"     -> object();
            case "["     -> array();
            case "true"  -> trueLiteral();
            case "false" -> falseLiteral();
            case "null"  -> nullLiteral();
            default      -> {
                if (scan.isConstant()) {
                    if (scan.isString()) yield stringLiteral();
                    else if (scan.isNumber()) yield numericLiteral();
                    else yield error("Expected constant, got "+ scan);
                } else {
                    yield error("Unrecognized start character (" + value + ")");
                }
            }
        };
    }

    public Tokens.Token end() {
        // TODO - this should make sure there is not en error
        //       meaning that the `state` stack is empty
        //       and the buffer is done.
        nextState = State.END;
        return new Tokens.NoToken();
    }

    public Tokens.Token object() {
        Scans.Scan scan = scanner.peekNextScan();
        
        if (scan.isEnd()) return error("object() expected more scanner tokens");
        if (scan.isError()) return error("Got error from scanner: " + scan.getValue());
        if (!scan.isOperator())
            return error("Expected end of object or start of property, but found (" + scan + ")");
        
        return switch (scan.getValue()) {
            case "{" -> {
                scanner.discardNextScan();
                context.push(Context.IN_OBJECT);
                stack.push(State.OBJECT);
                nextState = State.PROPERTY;
                yield new Tokens.StartObject();
            }
            case "," -> {
                if (stack.peek() == State.PROPERTY) {
                    yield endProperty();
                }
                
                scanner.discardNextScan();
                yield property();
            }
            case "}" -> {
                // TODO - check if the state is not empty
                if (stack.peek() == State.PROPERTY) {
                    yield endProperty();
                }
                scanner.discardNextScan();
                // TODO - check if the context is not empty and peek() == IN_OBJECT
                context.pop();
                // TODO - check if the stack is not empty and peek() == OBJECT
                stack.pop();
                nextState = stack.peek(); // restore the previous one
                yield new Tokens.EndObject();
            }
            default -> error("Expected end of object or start of property operator, but found (" + scan.getValue() + ")");
        };
    }

    public Tokens.Token property() {
        Scans.Scan scan = scanner.peekNextScan();
        
        if (scan.isEnd()) {
            return error("property() expected more scanner tokens");
        } else if (scan.isError()) {
            return error("Got error from scanner: "+ scan.getValue());
        } else if (scan.isString()) {
            context.push(Context.IN_PROPERTY);
            stack.push(State.PROPERTY);
            nextState = State.KEY_LITERAL;
            return new Tokens.StartProperty();
        } else if (scan.isOperator() && scan.getValue().equals(":")) {
            // TODO - check to be sure we are still in property state here
            scanner.discardNextScan();   // skip over the :
            Tokens.Token value = start(); // and grab whatever value we find
            // TODO - check if the Token is an ErrorToken, in which case just return it
            //       although perhaps we want to set an ERROR state too?? hmmm
            // TODO - check to be sure we are back in the same property state again
            if (nextState == null) nextState = State.END_PROPERTY;
            return value;
        }
        else {
            return object();
        }
    }

    public Tokens.Token endProperty() {
        // TODO - check if the context is not empty and peek() == IN_PROPERTY
        context.pop();
        // TODO - check if the stack is not empty and peek() == PROPERTY
        stack.pop(); // exit the property context
        nextState = State.OBJECT;
        return new Tokens.EndProperty();
    }

    public Tokens.Token array() {
        Scans.Scan scan = scanner.peekNextScan();
        
        if (scan.isEnd()) return error("array() expected more scanner tokens");;
        if (scan.isError()) return error("Got error from scanner: "+ scan.getValue());
        if (!scan.isOperator())
            return error("Expected end of array or start of an item, but found (" + scan + ")");
        
        return switch (scan.getValue()) {
            case "[" -> {
                scanner.discardNextScan();
                context.push(Context.IN_ARRAY);
                stack.push(State.ARRAY);
                nextState = State.ITEM;
                yield new Tokens.StartArray();
            }
            case "," -> {
                if (stack.peek() == State.ITEM) {
                    yield endItem();
                }
                scanner.discardNextScan();
                yield item();
            }
            case "]" -> {
                // TODO - check if the state is not empty
                if (stack.peek() == State.ITEM) {
                    yield endItem();
                }
                scanner.discardNextScan();
                // TODO - check if the context is not empty and peek() == IN_ARRAY
                context.pop();
                // TODO - check if the stack is not empty and peek() == ARRAY
                stack.pop();
                nextState = stack.peek(); // restore the previous one
                yield new Tokens.EndArray();
            }
            default -> error("Expected array or item, but found (" + scan.getValue() + ")");
        };
    }

    public Tokens.Token item() {
        Scans.Scan scan = scanner.peekNextScan();
        
        if (scan.isEnd()) {
            return error("array() expected more scanner tokens");
        } else if (scan.isError()) {
            return error("Got error from scanner: "+ scan.getValue());
        } else if (scan.isOperator() && scan.getValue().equals("]")) {
            return array();
        } else {
            // if we are in item context
            if (stack.peek() == State.END_ITEM) {
                Tokens.Token value = start(); // grab whatever value we find
                // TODO - check if the Token is an ErrorToken, in which case just return it
                //       although perhaps we want to set an ERROR state too?? hmmm
                // TODO - check to be sure we are back in the same item state again
                if (nextState == null) nextState = State.END_ITEM;
                return value;
            }
            
            // Handle starting the item
            // otherwise, start the item and "recurse"
            context.push(Context.IN_ITEM);
            stack.push(State.END_ITEM);
            nextState = State.ITEM;
            return new Tokens.StartItem();
        }
    }

    public Tokens.Token endItem() {
        // TODO - check if the state is not empty and peek() == ITEM
        // TODO - check if the context is not empty and peek() == IN_ARRAY
        context.pop();
        // TODO - check if the stack is not empty and peek() == ITEM
        stack.pop(); // exit the property context
        nextState = State.ARRAY;
        return new Tokens.EndItem();
    }
    
    public Tokens.Token keyLiteral() {
        Scans.Scan scan = scanner.getNextScan();
        if (scan.isEnd()) return error("Unexpected end of input, expected keyLiteral");
        if (scan.isError()) return error("Got error from scanner: "+ scan.getValue());
        nextState = State.PROPERTY; // return to caller state
        String key = scan.getValue();
        return new Tokens.AddKey(key.substring(1,key.lastIndexOf("\"")));
    }
    
    public Tokens.Token stringLiteral() {
        Scans.Scan scan = scanner.getNextScan();
        if (scan.isEnd()) return error("Unexpected end of input, expected stringLiteral");
        if (scan.isError()) return error("Got error from scanner: "+ scan.getValue());
        nextState = stack.peek(); // return to caller state
        String str = scan.getValue();
        return new Tokens.AddString(str.substring(1,str.lastIndexOf("\"")));
    }

    public Tokens.Token numericLiteral() {
        Scans.Scan scan = scanner.getNextScan();
        
        if (scan.isEnd()) return error("Unexpected end of input, expected numericLiteral");
        if (scan.isError()) return error("Got error from scanner: "+ scan.getValue());
        
        nextState = stack.peek(); // return to caller state
        if (scan.isInteger()) {
            return new Tokens.AddInt(Integer.parseInt(scan.getValue()));
        } else if (scan.isFloat()) {
            return new Tokens.AddFloat(Float.parseFloat(scan.getValue()));
        } else {
            return error("Expected Int or Float Scanner token, not "+ scan);
        }
    }

    public Tokens.Token falseLiteral() {
        scanner.discardNextScan();
        // TODO - check for errors and the correct token here
        return new Tokens.AddFalse();
    }

    public Tokens.Token trueLiteral() {
        scanner.discardNextScan();
        // TODO - check for errors and the correct token here
        return new Tokens.AddTrue();
    }

    public Tokens.Token nullLiteral() {
        scanner.discardNextScan();
        // TODO - check for errors and the correct token here
        return new Tokens.AddNull();
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

