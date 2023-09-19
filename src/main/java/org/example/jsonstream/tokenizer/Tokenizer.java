package org.example.jsonstream.tokenizer;

import java.util.Stack;
import java.util.stream.Stream;

public class Tokenizer implements TokenProducer {

    public enum Context {
        IN_ROOT,
        IN_ERROR,
        IN_OBJECT, IN_PROPERTY,
        IN_ARRAY, IN_ITEM
    };
    
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
        
        token.setContext(context.toArray(new Context[]{}));

        return token;
    }

    public Tokens.Token root() {
        if (scanner.hasMore()) {
            ScannerToken nextToken = scanner.peekNextToken();
            
            if (nextToken.isError()) {
                return error("Got error from scanner: "+nextToken.getValue());
            }
            
            return switch (nextToken.getValue()) {
                case "{" -> object();
                case "[" -> array();
                default -> error("The root node must be either an Object({}) or an Array([])");
            };
        } else {
            return end();
        }
    }

    public Tokens.Token start() {
        if (scanner.hasMore()) {
            ScannerToken nextToken = scanner.peekNextToken();
            
            if (nextToken.isError()) {
                return error("Got error from scanner: "+nextToken.getValue());
            }
            
            String value = nextToken.getValue();
            return switch (value) {
                case "{"     -> object();
                case "["     -> array();
                case "true"  -> trueLiteral();
                case "false" -> falseLiteral();
                case "null"  -> nullLiteral();
                default      -> {
                    if (nextToken.isConstant()) {
                        if (nextToken.isString()) yield stringLiteral();
                        else if (nextToken.isNumber()) yield numericLiteral();
                        else yield error("Expected constant, got "+nextToken);
                    } else {
                        yield error("Unrecognized start character (" + value + ")");
                    }
                }
            };
        } else {
            return end();
        }
    }

    public Tokens.Token end() {
        // TODO - this should make sure there is not en error
        //       meaning that the `state` stack is empty
        //       and the buffer is done.
        nextState = State.END;
        return new Tokens.NoToken();
    }

    public Tokens.Token object() {
        if (scanner.hasMore()) {
            ScannerToken nextToken = scanner.peekNextToken();
            
            if (nextToken.isError()) {
                return error("Got error from scanner: "+nextToken.getValue());
            }
            
            if (!nextToken.isOperator()) {
                return error("Expected end of object or start of property, but found (" + nextToken.toString() + ")");
            }
            
            return switch (nextToken.getValue()) {
                case "{" -> {
                    scanner.discardNextToken();
                    context.push(Context.IN_OBJECT);
                    stack.push(State.OBJECT);
                    nextState = State.PROPERTY;
                    yield new Tokens.StartObject();
                }
                case "," -> {
                    if (stack.peek() == State.PROPERTY) {
                        yield endProperty();
                    }
                    
                    scanner.discardNextToken();
                    yield property();
                }
                case "}" -> {
                    // TODO - check if the state is not empty
                    if (stack.peek() == State.PROPERTY) {
                        yield endProperty();
                    }
                    scanner.discardNextToken();
                    // TODO - check if the context is not empty and peek() == IN_OBJECT
                    context.pop();
                    // TODO - check if the stack is not empty and peek() == OBJECT
                    stack.pop();
                    nextState = stack.peek(); // restore the previous one
                    yield new Tokens.EndObject();
                }
                default -> error("Expected end of object or start of property operator, but found (" + nextToken.getValue() + ")");
            };
        } else {
            return error("object() expected more scanner tokens");
        }
    }

    public Tokens.Token property() {
        if (scanner.hasMore()) {
            ScannerToken nextToken = scanner.peekNextToken();
            
            if (nextToken.isError()) {
                return error("Got error from scanner: "+nextToken.getValue());
            } else if (nextToken.isString()) {
                context.push(Context.IN_PROPERTY);
                stack.push(State.PROPERTY);
                nextState = State.KEY_LITERAL;
                return new Tokens.StartProperty();
            } else if (nextToken.isOperator() && nextToken.getValue().equals(":")) {
                // TODO - check to be sure we are still in property state here
                scanner.discardNextToken();   // skip over the :
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
            
        } else {
            return error("property() expected more scanner tokens");
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
        if (scanner.hasMore()) {
            ScannerToken nextToken = scanner.peekNextToken();
            
            if (nextToken.isError()) {
                return error("Got error from scanner: "+nextToken.getValue());
            }
            
            if (!nextToken.isOperator()) {
                return error("Expected end of array or start of an item, but found (" + nextToken.toString() + ")");
            }
            
            return switch (nextToken.getValue()) {
                case "[" -> {
                    scanner.discardNextToken();
                    context.push(Context.IN_ARRAY);
                    stack.push(State.ARRAY);
                    nextState = State.ITEM;
                    yield new Tokens.StartArray();
                }
                case "," -> {
                    if (stack.peek() == State.ITEM) {
                        yield endItem();
                    }
                    scanner.discardNextToken();
                    yield item();
                }
                case "]" -> {
                    // TODO - check if the state is not empty
                    if (stack.peek() == State.ITEM) {
                        yield endItem();
                    }
                    scanner.discardNextToken();
                    // TODO - check if the context is not empty and peek() == IN_ARRAY
                    context.pop();
                    // TODO - check if the stack is not empty and peek() == ARRAY
                    stack.pop();
                    nextState = stack.peek(); // restore the previous one
                    yield new Tokens.EndArray();
                }
                default -> error("Expected array or item, but found (" + nextToken.getValue() + ")");
            };
        }
        else {
            return error("array() expected more scanner tokens");
        }
    }

    public Tokens.Token item() {
        if (scanner.hasMore()) {
            ScannerToken nextToken = scanner.peekNextToken();
            
            if (nextToken.isError()) {
                return error("Got error from scanner: "+nextToken.getValue());
            }
            
            if (nextToken.isOperator() && nextToken.getValue().equals("]")) {
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
        else {
            return error("array() expected more scanner tokens");
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
        ScannerToken nextToken = scanner.getNextToken();
        if (nextToken.isError()) {
            return error("Got error from scanner: "+nextToken.getValue());
        }
        nextState = State.PROPERTY; // return to caller state
        return new Tokens.AddKey(nextToken.getValue());
    }
    
    public Tokens.Token stringLiteral() {
        ScannerToken nextToken = scanner.getNextToken();
        if (nextToken.isError()) {
            return error("Got error from scanner: "+nextToken.getValue());
        }
        nextState = stack.peek(); // return to caller state
        return new Tokens.AddString(nextToken.getValue());
    }

    public Tokens.Token numericLiteral() {
        ScannerToken nextToken = scanner.getNextToken();
        
        if (nextToken.isError()) {
            return error("Got error from scanner: "+nextToken.getValue());
        }
        
        nextState = stack.peek(); // return to caller state
        if (nextToken.isInteger()) {
            return new Tokens.AddInt(Integer.parseInt(nextToken.getValue()));
        } else if (nextToken.isFloat()) {
            return new Tokens.AddFloat(Float.parseFloat(nextToken.getValue()));
        } else {
            return error("Expected Int or Float Scanner token, not "+nextToken);
        }
    }

    public Tokens.Token falseLiteral() {
        ScannerToken nextToken = scanner.getNextToken();
        // TODO - check for errors and the correct token here
        return new Tokens.AddFalse();
    }

    public Tokens.Token trueLiteral() {
        ScannerToken nextToken = scanner.getNextToken();
        // TODO - check for errors and the correct token here
        return new Tokens.AddTrue();
    }

    public Tokens.Token nullLiteral() {
        ScannerToken nextToken = scanner.getNextToken();
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

