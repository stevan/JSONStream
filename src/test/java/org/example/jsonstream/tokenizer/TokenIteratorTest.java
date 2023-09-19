package org.example.jsonstream.tokenizer;

import org.junit.jupiter.api.Test;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class TokenIteratorTest {
    
    @Test
    void IteratorTest() {
        Tokenizer t = new Tokenizer(new Scanner("[]"));
        TokenIterator i = t.iterator();
        
        assertTrue(i.hasNext());
        assertEquals(Tokens.Type.START_ARRAY, i.next().getType());
        
        assertTrue(i.hasNext());
        assertEquals(Tokens.Type.END_ARRAY, i.next().getType());
        
        assertFalse(i.hasNext());
        assertThrows(NoSuchElementException.class, i::next);
    }
    
    @Test
    void IteratorTest_withoutCallingHasNext() {
        
        Tokenizer t = new Tokenizer(new Scanner("[]"));
        TokenIterator i = t.iterator();
        
        assertEquals(Tokens.Type.START_ARRAY, i.next().getType());
        assertEquals(Tokens.Type.END_ARRAY, i.next().getType());
        
        assertFalse(i.hasNext());
        assertThrows(NoSuchElementException.class, i::next);
    }
    
    @Test
    void IteratorTest_NoTokenizer() {
        
        Tokenizer t = new Tokenizer(new Scanner("[]"));
        Iterator<Tokens.Token> i = t.stream().toList().iterator();
        
        assertTrue(i.hasNext());
        assertEquals(Tokens.Type.START_ARRAY, i.next().getType());
        
        assertTrue(i.hasNext());
        assertEquals(Tokens.Type.END_ARRAY, i.next().getType());
        
        assertFalse(i.hasNext());
        assertThrows(NoSuchElementException.class, i::next);
    }
    
    @Test
    void IteratorTest_NoTokenizer_withoutCallingHasNext() {
        
        Tokenizer t = new Tokenizer(new Scanner("[]"));
        Iterator<Tokens.Token> i = t.stream().toList().iterator();
        
        assertEquals(Tokens.Type.START_ARRAY, i.next().getType());
        assertEquals(Tokens.Type.END_ARRAY, i.next().getType());
        
        assertFalse(i.hasNext());
        assertThrows(NoSuchElementException.class, i::next);
    }
}
