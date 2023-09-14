package org.example.jsonstream.query;

import org.example.jsonstream.tokenizer.*;
import java.util.*;
import java.util.stream.Collectors;

public class QueryEngine {
    
    public static class QueryException extends Exception {
        public QueryException (String msg) {
            super(msg);
        }
    }

    public static class ObjectQuery {
        
        Map<String,Tokens.Type> keys = new HashMap<>();
        Map<String,Tokens.Token> results = new HashMap<>();
        
        public Map<String,Tokens.Token> getResults() { return results; }
    
        public ObjectQuery getValueForKey(String key, Tokens.Type valueType) throws QueryException {
            switch (valueType) {
                case ADD_INT:
                case ADD_FLOAT:
                case ADD_STRING:
                case ADD_TRUE:
                case ADD_FALSE:
                case ADD_NULL:
                    break;
                default:
                    throw new QueryException("You can only get Scalar values (ADD_INT, ADD_FLOAT, ADD_*)");
            }
            keys.put(key, valueType);
            return this;
        }
        
        public void execute(Tokenizer tokenizer) throws QueryException {
            
            Tokens.Token startObjectToken = tokenizer.produceToken();
            
            if (startObjectToken.getType() == Tokens.Type.START_OBJECT) {
                
                Optional<Tokens.Token> maybeBadToken = consumeProperties(tokenizer);
                do {
                    // if we have a bad token
                    if (maybeBadToken.isPresent()) {
                        Tokens.Token t = maybeBadToken.get();
                        // we need to check it
                        if (t.getType() != Tokens.Type.END_OBJECT) {
                            throw new QueryException("Expected END_OBJECT after the value, not " + t.getType());
                        }
                        // otherwise, we can break out of the loop
                        break;
                    }
                    else {
                        maybeBadToken = consumeProperties(tokenizer);
                    }
                } while (maybeBadToken.isEmpty());
            }
            else {
                throw new QueryException("ObjectQuery must start with an START_OBJECT token");
            }
        }
        
        private Optional<Tokens.Token> consumeProperties(Tokenizer tokenizer) throws QueryException {
            Tokens.Token startPropToken = tokenizer.produceToken();
            
            // if the next token is not a StartProperty
            // then it is either an error, or an EndObject
            // either way we kick it back up to execute
            // to figure this out
            if (startPropToken.getType() != Tokens.Type.START_PROPERTY) {
                return Optional.of(startPropToken);
            }
            
            // now get the next token
            Tokens.Token addKeyToken = tokenizer.produceToken();
            if (addKeyToken.getType() == Tokens.Type.ADD_KEY) {
                String key = ((Tokens.AddKey) addKeyToken).getValue();
                
                if (keys.containsKey(key)) {
                    Tokens.Type expectedType = keys.get(key);
                    
                    Tokens.Token nextToken = tokenizer.produceToken();
                    if (nextToken.getType() == expectedType) {
                        results.put(key, nextToken);
                    }
                    else {
                        throw new QueryException("Expected token("+expectedType+") but got token("+nextToken.getName()+") instead");
                    }
                    
                    Tokens.Token endPropToken = tokenizer.produceToken();
                    if (endPropToken.getType() != Tokens.Type.END_PROPERTY) {
                        throw new QueryException("Expected END_PROPERTY after the value");
                    }
                }
                else {
                    Integer depth = startPropToken.getContextDepth();
                    // skip all the tokens until the END_PROPERTY
                    tokenizer.asStream()
                        .takeWhile((t)-> t.getType() != Tokens.Type.END_PROPERTY
                                             || t.getContextDepth() > depth)
                        .collect(Collectors.toList());
                    
                    System.out.println(tokenizer.getBuffer().toString());
                }
            }
            else {
                // this should never really happen because
                // the tokenize will have enforced the
                // correct sequence of tokens, but we
                // check it just in case something is awry
                throw new QueryException("Expected ADD_KEY token after START_PROPERTY token");
            }
            
            return Optional.empty();
        }
        
    }
    
    
}
