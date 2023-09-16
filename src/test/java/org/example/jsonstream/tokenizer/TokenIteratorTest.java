package org.example.jsonstream.tokenizer;

import org.junit.jupiter.api.Test;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class TokenIteratorTest {
    
    @Test
    void IteratorTest() {
        
        CharacterStream b = new CharacterStream("[]");
        Tokenizer t = new Tokenizer(b);
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
        
        CharacterStream b = new CharacterStream("[]");
        Tokenizer t = new Tokenizer(b);
        TokenIterator i = t.iterator();
        
        assertEquals(Tokens.Type.START_ARRAY, i.next().getType());
        assertEquals(Tokens.Type.END_ARRAY, i.next().getType());
        
        assertFalse(i.hasNext());
        assertThrows(NoSuchElementException.class, i::next);
    }
    
    @Test
    void IteratorTest_NoTokenizer() {
        
        CharacterStream b = new CharacterStream("[]");
        Tokenizer t = new Tokenizer(b);
        Iterator<Tokens.Token> i = t.stream().collect(Collectors.toList()).iterator();
        
        assertTrue(i.hasNext());
        assertEquals(Tokens.Type.START_ARRAY, i.next().getType());
        
        assertTrue(i.hasNext());
        assertEquals(Tokens.Type.END_ARRAY, i.next().getType());
        
        assertFalse(i.hasNext());
        assertThrows(NoSuchElementException.class, i::next);
    }
    
    @Test
    void IteratorTest_NoTokenizer_withoutCallingHasNext() {
        
        CharacterStream b = new CharacterStream("[]");
        Tokenizer t = new Tokenizer(b);
        Iterator<Tokens.Token> i = t.stream().collect(Collectors.toList()).iterator();
        
        assertEquals(Tokens.Type.START_ARRAY, i.next().getType());
        assertEquals(Tokens.Type.END_ARRAY, i.next().getType());
        
        assertFalse(i.hasNext());
        assertThrows(NoSuchElementException.class, i::next);
    }
}
