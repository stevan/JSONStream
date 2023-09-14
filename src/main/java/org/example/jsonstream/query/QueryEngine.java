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
        
        Set<String> keys = new HashSet<>();
        Map<String,List<Tokens.Token>> results = new HashMap<>();
        
        public Map<String,List<Tokens.Token>> getResults() {
            return results;
        }
    
        public ObjectQuery getValueFor(String key) {
            keys.add(key);
            return this;
        }
        
        public void execute(TokenProducer tokenizer) throws QueryException {
            
            Tokens.Token startObjectToken = tokenizer.produceToken();
            
            if (!(startObjectToken instanceof Tokens.StartObject)) {
                throw new QueryException("ObjectQuery must start with an StartObject token, not "+startObjectToken.getName());
            }
            
            // consume the first property
            Optional<Tokens.Token> badToken = consumeProperties(tokenizer);
            do {
                // if we have a bad token
                if (badToken.isPresent()) {
                    Tokens.Token t = badToken.get();
                    // we need to check it
                    if (!(t instanceof Tokens.EndObject)) {
                        throw new QueryException("Expected EndObject token after the property, not " + t.getType());
                    }
                    // otherwise, we will break out of the loop
                    // because badToken will be Present
                    // and therefor, not Empty which is the
                    // loop conditional :)
                }
                else {
                    // if we do not have a bad token,
                    // then consume the next property
                    badToken = consumeProperties(tokenizer);
                }
            } while (badToken.isEmpty());
        }
        
        private Optional<Tokens.Token> consumeProperties(TokenProducer tokenizer) {
            Tokens.Token startPropToken = tokenizer.produceToken();
            
            // if the next token is not a StartProperty
            // then it is either an error(), end(), or
            // an EndObject, either way we kick it back
            // up to execute to figure this out
            if (!(startPropToken instanceof Tokens.StartProperty)) {
                return Optional.of(startPropToken);
            }
            
            // now get the next token
            Tokens.Token addKeyToken = tokenizer.produceToken();
            
            // if it is not an AddKey token, then it will
            // likely be an error() or end() token, as the
            // tokenizer would not emit anything else in
            // this case
            if (!(addKeyToken instanceof Tokens.AddKey)) {
                // again we kick it back up to execute
                // to work things out and throw the
                // exception as needed
                return Optional.of(addKeyToken);
            }
            
            String key = ((Tokens.AddKey) addKeyToken).getValue();
            
            // collect all the tokens until the END_PROPERTY
            // at the same depth as we started.
            Integer depth = startPropToken.getContextDepth();
            List<Tokens.Token> valueTokens = tokenizer.stream()
                                                 .takeWhile((t) -> t.getType() != Tokens.Type.END_PROPERTY
                                                                       || t.getContextDepth() > depth)
                                                 .collect(Collectors.toList());
            
            // we collect these if we need them or not
            // because we need to advance the tokenizer
            // so if we don't need them, we simply do
            // nothing, on purpose
            if (keys.contains(key)) {
                results.put(key, valueTokens);
            }

            // and finally we return empty()
            // because we have no bad tokens
            // (:
            return Optional.empty();
        }
    }
}
