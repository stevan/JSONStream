package org.example.jsonstream.tokenizer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenConsumerTest {
    
    public static class BasicConsumer implements TokenConsumer {
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
        
        public void consumeToken(Tokens.NoToken token) { fail(); }
        public void consumeToken(Tokens.ErrorToken token) { fail(); }
        public void consumeToken(Tokens.StartObject token) { fail(); }
        public void consumeToken(Tokens.EndObject token) { fail(); }
        public void consumeToken(Tokens.StartProperty token) { fail(); }
        public void consumeToken(Tokens.EndProperty token) { fail(); }
        public void consumeToken(Tokens.AddKey token) { fail(); }
        public void consumeToken(Tokens.AddString token) { fail(); }
        public void consumeToken(Tokens.AddFloat token) { fail(); }
        public void consumeToken(Tokens.AddTrue token) { fail(); }
        public void consumeToken(Tokens.AddFalse token) { fail(); }
        public void consumeToken(Tokens.AddNull token) { fail(); }
    }
    
    @Test
    void consumeToken_Basic() {
        CharBuffer b = new CharBuffer("[10]");
        Tokenizer t = new Tokenizer(b);
        
        BasicConsumer o = new BasicConsumer();
        BasicConsumer o2 = new BasicConsumer();
        
        t.stream()
            .peek(o2::consumeToken)
            .forEach(o::consumeToken);
        
        assertEquals(5, o.getCount());
        assertEquals(5, o2.getCount());
    }
}