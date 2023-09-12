package org.example.jsonstream.tokenizer;

import org.junit.jupiter.api.Test;

class TokenConsumerTest {
    
    @Test
    void consumeToken_Basic() {
        CharBuffer b = new CharBuffer("[10]");
        Tokenizer t = new Tokenizer(b);
        
        TokenConsumer o = new TokenConsumer();
        
        t.asStream().forEach(o::consumeToken);
    }
}