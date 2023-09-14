package org.example.jsonstream.tokenizer;

import java.util.Iterator;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class TokenIterator implements Iterator<Tokens.Token> {
    private final BooleanSupplier hasNext;
    private final Supplier<Tokens.Token> next;
    public TokenIterator(BooleanSupplier h, Supplier<Tokens.Token> n) {
        hasNext = h;
        next = n;
    }
    
    public boolean hasNext() {
        return hasNext.getAsBoolean();
    }
    
    public Tokens.Token next() {
        return next.get();
    }
}