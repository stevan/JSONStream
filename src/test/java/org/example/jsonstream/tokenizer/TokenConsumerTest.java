package org.example.jsonstream.tokenizer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenConsumerTest {
    
    // TODO - make a test consumer that asserts
    //  false for any of the consumeToken methods
    //  and you have to override to make it work,
    //  might not be worth the effort actually, have to see
    public class BasicConsumer extends TokenConsumer {
        int count = 0;
        
        public int getCount() { return count; }
        
        public void consumeToken(Tokens.StartArray t) {
            count++;
            assertTrue(true);
        }
        
        public void consumeToken(Tokens.StartItem t) {
            count++;
            assertTrue(true);
        }
        
        public void consumeToken(Tokens.EndItem t) {
            count++;
            assertTrue(true);
        }
        
        public void consumeToken(Tokens.EndArray t) {
            count++;
            assertTrue(true);
        }
        
        public void consumeToken(Tokens.AddInt t) {
            count++;
            assertEquals(t.getValue(), 10);
        }
    }
    
    @Test
    void consumeToken_Basic() {
        CharBuffer b = new CharBuffer("[10]");
        Tokenizer t = new Tokenizer(b);
        
        BasicConsumer o = new BasicConsumer();
        
        t.stream().peek(System.out::println).forEach(o::consumeToken);
        
        assertEquals(5, o.getCount());
    }
}