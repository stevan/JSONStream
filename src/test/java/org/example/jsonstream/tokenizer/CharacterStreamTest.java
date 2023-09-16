package org.example.jsonstream.tokenizer;

import org.example.jsonstream.tokenizer.CharacterStream;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class CharacterStreamTest {

    @Test
    void get() {
        CharacterStream c = new CharacterStream("1,2,3");
        assertDoesNotThrow(() -> {
            assertFalse(c.isDone());
            assertEquals(c.getNext().get(), '1');
            assertFalse(c.isDone());
            assertEquals(c.getNext().get(), ',');
            assertFalse(c.isDone());
            assertEquals(c.getNext().get(), '2');
            assertFalse(c.isDone());
            assertEquals(c.getNext().get(), ',');
            assertFalse(c.isDone());
            assertEquals(c.getNext().get(), '3');
            assertTrue(c.isDone());
        });
        assertThrows(NoSuchElementException.class, () -> c.getNext().get());
    }

    @Test
    void peek() {
        CharacterStream c = new CharacterStream("1");
        assertDoesNotThrow(() -> {
            assertFalse(c.isDone());
            assertEquals(c.peek().get(), '1');
            assertFalse(c.isDone());
            assertEquals(c.peek().get(), '1');
            c.skip(1);
            assertTrue(c.isDone());
        });
        assertThrows(NoSuchElementException.class, () -> c.peek().get());
    }

    @Test
    void isDone() {
        CharacterStream c = new CharacterStream("1,2,3");
        assertFalse(c.isDone());
        c.skip(5);
        assertTrue(c.isDone());
        assertThrows(NoSuchElementException.class, () -> c.getNext().get());
    }

    @Test
    void skip() {
        CharacterStream c = new CharacterStream("1,2,3,4,5");
        assertDoesNotThrow(() -> {
            assertFalse(c.isDone());
            assertEquals(c.getNext().get(), '1');
            c.skip(1);
            assertFalse(c.isDone());
            assertEquals(c.getNext().get(), '2');
            c.skip(1);
            assertFalse(c.isDone());
            assertEquals(c.getNext().get(), '3');
            c.skip(3);
            assertFalse(c.isDone());
            assertEquals(c.getNext().get(), '5');
            assertTrue(c.isDone());
        });
        assertThrows(NoSuchElementException.class, () -> c.getNext().get());
    }

    @Test
    void skipWhitespaceAndPeek() {
        CharacterStream c = new CharacterStream(" 1  2   3    4     ");
        assertFalse(c.isDone());
        assertDoesNotThrow(() -> {
            assertEquals(c.skipWhitespaceAndPeek().get(), '1');
            assertEquals(c.peek().get(), '1');
            assertFalse(c.isDone());
            assertEquals(c.getNext().get(), '1');
            assertFalse(c.isDone());

            assertEquals(c.skipWhitespaceAndPeek().get(), '2');
            assertEquals(c.peek().get(), '2');
            assertFalse(c.isDone());
            assertEquals(c.getNext().get(), '2');
            assertFalse(c.isDone());

            assertEquals(c.skipWhitespaceAndPeek().get(), '3');
            assertEquals(c.peek().get(), '3');
            assertFalse(c.isDone());
            assertEquals(c.getNext().get(), '3');
            assertFalse(c.isDone());

            assertEquals(c.skipWhitespaceAndPeek().get(), '4');
            assertEquals(c.peek().get(), '4');
            assertFalse(c.isDone());
            assertEquals(c.getNext().get(), '4');
            assertFalse(c.isDone());
        });
        assertThrows(NoSuchElementException.class, () -> c.skipWhitespaceAndPeek().get());
        assertTrue(c.isDone());
    }
}
