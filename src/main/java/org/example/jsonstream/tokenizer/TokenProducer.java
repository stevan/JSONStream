package org.example.jsonstream.tokenizer;

import java.util.stream.Stream;
import java.util.List;
import java.util.Iterator;

public interface TokenProducer {
    Tokens.Token produceToken();
    boolean isDone();
    Stream<Tokens.Token> stream();
    
    static TokenProducer of(List<Tokens.Token> tokens) {
        Iterator<Tokens.Token> i = tokens.iterator();
        // TODO - make this a concrete class, but
        //  since it is not likely to be used a lot
        //  then we can put this off ;)
        return new TokenProducer(){
            public Tokens.Token produceToken() {
                if (i.hasNext()) return i.next();
                return new Tokens.NoToken();
            }
            public boolean isDone() { return i.hasNext(); }
            public Stream<Tokens.Token> stream() {
                return Stream.iterate(
                    produceToken(),
                    (t) -> !t.isTerminal(),
                    (t) -> produceToken()
                );
            }
        };
    }
}
