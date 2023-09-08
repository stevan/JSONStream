package org.example.jsonstream;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class CharBufferTest {

    @Test
    void get() {
        CharBuffer c = new CharBuffer("1,2,3");
        assertFalse(c.isDone());
        assertEquals(c.get().get(), '1');
        assertFalse(c.isDone());
        assertEquals(c.get().get(), ',');
        assertFalse(c.isDone());
        assertEquals(c.get().get(), '2');
        assertFalse(c.isDone());
        assertEquals(c.get().get(), ',');
        assertFalse(c.isDone());
        assertEquals(c.get().get(), '3');
        assertTrue(c.isDone());
        assertThrows(NoSuchElementException.class, () -> { c.get().get(); });
    }

    @Test
    void peek() {
        CharBuffer c = new CharBuffer("1");
        assertFalse(c.isDone());
        assertEquals(c.peek().get(), '1');
        assertFalse(c.isDone());
        assertEquals(c.peek().get(), '1');
        c.skip();
        assertTrue(c.isDone());
        assertThrows(NoSuchElementException.class, () -> { c.peek().get(); });
    }

    @Test
    void isDone() {
        CharBuffer c = new CharBuffer("1,2,3");
        assertFalse(c.isDone());
        c.skip(5);
        assertTrue(c.isDone());
        assertThrows(NoSuchElementException.class, () -> { c.get().get(); });
    }

    @Test
    void skip() {
        CharBuffer c = new CharBuffer("1,2,3,4,5");
        assertFalse(c.isDone());
        assertEquals(c.get().get(), '1');
        c.skip(1);
        assertFalse(c.isDone());
        assertEquals(c.get().get(), '2');
        c.skip(1);
        assertFalse(c.isDone());
        assertEquals(c.get().get(), '3');
        c.skip(3);
        assertFalse(c.isDone());
        assertEquals(c.get().get(), '5');
        assertTrue(c.isDone());
        assertThrows(NoSuchElementException.class, () -> { c.get().get(); });
    }

    @Test
    void skipWhitespaceAndPeek() {
        CharBuffer c = new CharBuffer(" 1  2   3    4     ");
        assertFalse(c.isDone());

        assertEquals(c.skipWhitespaceAndPeek().get(), '1');
        assertEquals(c.peek().get(), '1');
        assertFalse(c.isDone());
        assertEquals(c.get().get(), '1');
        assertFalse(c.isDone());

        assertEquals(c.skipWhitespaceAndPeek().get(), '2');
        assertEquals(c.peek().get(), '2');
        assertFalse(c.isDone());
        assertEquals(c.get().get(), '2');
        assertFalse(c.isDone());

        assertEquals(c.skipWhitespaceAndPeek().get(), '3');
        assertEquals(c.peek().get(), '3');
        assertFalse(c.isDone());
        assertEquals(c.get().get(), '3');
        assertFalse(c.isDone());

        assertEquals(c.skipWhitespaceAndPeek().get(), '4');
        assertEquals(c.peek().get(), '4');
        assertFalse(c.isDone());
        assertEquals(c.get().get(), '4');
        assertFalse(c.isDone());

        assertThrows(NoSuchElementException.class, () -> { c.skipWhitespaceAndPeek().get(); });
        assertTrue(c.isDone());
    }
}