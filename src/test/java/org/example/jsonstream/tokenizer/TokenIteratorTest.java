package org.example.jsonstream.tokenizer;

import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class TokenIteratorTest {
    
    @Test
    void IteratorTest() {
        
        CharBuffer b = new CharBuffer("[]");
        Tokenizer t = new Tokenizer(b);
        TokenIterator i = t.iterator();
        
        assertTrue(i.hasNext());
        assertEquals(i.next().getType(), Tokens.Type.START_ARRAY);
        
        assertTrue(i.hasNext());
        assertEquals(i.next().getType(), Tokens.Type.END_ARRAY);
        
        assertTrue(i.hasNext());
        assertEquals(i.next().getType(), Tokens.Type.NO_TOKEN);
        
        assertFalse(i.hasNext());
    }
}