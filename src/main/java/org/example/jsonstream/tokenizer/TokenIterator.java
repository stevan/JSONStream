package org.example.jsonstream.tokenizer;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class TokenIterator implements Iterator<Tokens.Token> {
    
    private final Tokenizer tokenizer;
    
    private Tokens.Token nextToken;
    private Boolean shouldAdvance = true;
    
    public TokenIterator(Tokenizer t) {
        tokenizer = t;
    }
    
    public boolean hasNext() {
        if (tokenizer.isDone()) return false;
        if (shouldAdvance) {
            nextToken = tokenizer.produceToken();
            shouldAdvance = false;
        }
        return !nextToken.isTerminal();
    }
    
    public Tokens.Token next() {
        if (shouldAdvance) {
            nextToken = tokenizer.produceToken();
        }
        if (nextToken.isTerminal()) throw new NoSuchElementException();
        shouldAdvance = true;
        return nextToken;
    }
}