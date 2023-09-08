package org.example.jsonstream;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TokenizerTest {

    private void advanceNextTokenAndCheck(Tokenizer tokenizer, CharBuffer buffer, Class<? extends Tokenizer.Token> tokenClass, Tokenizer.State peek, Tokenizer.State nextState) {
        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = tokenizer.produceToken(buffer);
            assertTrue(token.isPresent(), "got a token");
            //System.out.println(token.get().toString() + " -> " + tokenizer.state.toString() + " : " + tokenizer.nextState);
            assertInstanceOf(tokenClass, token.get(), "token is the expected instance");
        });

        assertSame(peek, tokenizer.state.peek(), "state.peek() is as expected");
        assertSame(nextState, tokenizer.nextState, "nextState is as expected");
    }

    private void advanceNextTokenAndCheckAddString(Tokenizer tokenizer, CharBuffer buffer, Tokenizer.State peek, Tokenizer.State nextState, String expected_str) {
        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = tokenizer.produceToken(buffer);
            assertTrue(token.isPresent());
            assertInstanceOf(Tokenizer.AddString.class, token.get());

            Tokenizer.AddString str = (Tokenizer.AddString) token.get();
            assertEquals(str.getValue(), expected_str);

        });

        assertSame(peek, tokenizer.state.peek());
        assertSame(nextState, tokenizer.nextState);
    }

    @Test
    void produceToken_ErrorToken() {
        CharBuffer b = new CharBuffer("1");
        Tokenizer t = new Tokenizer();

        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = t.produceToken(b);
            assertTrue(token.isPresent());
            assertInstanceOf(Tokenizer.ErrorToken.class, token.get());
            Tokenizer.ErrorToken err = (Tokenizer.ErrorToken) token.get();
            assertEquals(err.getMsg(), "The root node must be either an Object({}) or an Array([])");
        });

        // TODO: this should probaby be the ERROR state
        assertSame(t.state.peek(), Tokenizer.State.ROOT);
    }

    @Test
    void produceToken_NoToken() {
        CharBuffer b = new CharBuffer("");
        Tokenizer t = new Tokenizer();

        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = t.produceToken(b);
            assertTrue(token.isPresent());
            assertInstanceOf(Tokenizer.NoToken.class, token.get());
        });

        assertSame(t.state.peek(), Tokenizer.State.ROOT);
    }

    @Test
    void produceToken_StartObjectToken() {
        CharBuffer b = new CharBuffer("{");
        Tokenizer t = new Tokenizer();

        advanceNextTokenAndCheck(t, b, Tokenizer.StartObject.class, Tokenizer.State.OBJECT, Tokenizer.State.PROPERTY);
    }


    @Test
    void produceToken_StartAndEndObjectToken() {
        CharBuffer b = new CharBuffer("{}");
        Tokenizer t = new Tokenizer();

        advanceNextTokenAndCheck(t, b, Tokenizer.StartObject.class, Tokenizer.State.OBJECT, Tokenizer.State.PROPERTY);
        advanceNextTokenAndCheck(t, b, Tokenizer.EndObject.class, Tokenizer.State.ROOT, Tokenizer.State.ROOT);
    }

    @Test
    void produceToken_StartAndEndObjectTokenWithKey() {
        CharBuffer b = new CharBuffer("{\"foo\"");
        Tokenizer t = new Tokenizer();

        advanceNextTokenAndCheck(t, b, Tokenizer.StartObject.class, Tokenizer.State.OBJECT, Tokenizer.State.PROPERTY);
        advanceNextTokenAndCheck(t, b, Tokenizer.StartProperty.class, Tokenizer.State.PROPERTY, Tokenizer.State.STRING_LITERAL);
        advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, "foo");
    }
    
    @Test
    void produceToken_ObjectTokensWithSingleProperty() {
        CharBuffer b = new CharBuffer("{\"foo\":\"bar\"}");
        Tokenizer t = new Tokenizer();

        advanceNextTokenAndCheck(t, b, Tokenizer.StartObject.class, Tokenizer.State.OBJECT, Tokenizer.State.PROPERTY);
        advanceNextTokenAndCheck(t, b, Tokenizer.StartProperty.class, Tokenizer.State.PROPERTY, Tokenizer.State.STRING_LITERAL);
        advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, "foo");
        advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, "bar");
        advanceNextTokenAndCheck(t, b, Tokenizer.EndProperty.class, Tokenizer.State.OBJECT, Tokenizer.State.OBJECT);
        advanceNextTokenAndCheck(t, b, Tokenizer.EndObject.class, Tokenizer.State.ROOT, Tokenizer.State.ROOT);
    }
    
    @Test
    void produceToken_ObjectTokensWithSinglePropertyAndComplexValue() {
        CharBuffer b = new CharBuffer("{\"foo\":{}}");
        Tokenizer t = new Tokenizer();

        advanceNextTokenAndCheck(t, b, Tokenizer.StartObject.class, Tokenizer.State.OBJECT, Tokenizer.State.PROPERTY);
        advanceNextTokenAndCheck(t, b, Tokenizer.StartProperty.class, Tokenizer.State.PROPERTY, Tokenizer.State.STRING_LITERAL);
        advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, "foo");
        advanceNextTokenAndCheck(t, b, Tokenizer.StartObject.class, Tokenizer.State.OBJECT, Tokenizer.State.PROPERTY);
        advanceNextTokenAndCheck(t, b, Tokenizer.EndObject.class, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY);
        advanceNextTokenAndCheck(t, b, Tokenizer.EndProperty.class, Tokenizer.State.OBJECT, Tokenizer.State.OBJECT);
        advanceNextTokenAndCheck(t, b, Tokenizer.EndObject.class, Tokenizer.State.ROOT, Tokenizer.State.ROOT);
    }
    
    @Test
    void produceToken_ObjectTokensWithTwoProperties() {
        CharBuffer b = new CharBuffer("{\"foo\":\"bar\",\"baz\":\"gorch\"}");
        Tokenizer t = new Tokenizer();
        
        advanceNextTokenAndCheck(t, b, Tokenizer.StartObject.class, Tokenizer.State.OBJECT, Tokenizer.State.PROPERTY);
        advanceNextTokenAndCheck(t, b, Tokenizer.StartProperty.class, Tokenizer.State.PROPERTY, Tokenizer.State.STRING_LITERAL);
        advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, "foo");
        advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, "bar");
        advanceNextTokenAndCheck(t, b, Tokenizer.EndProperty.class, Tokenizer.State.OBJECT, Tokenizer.State.OBJECT);
        advanceNextTokenAndCheck(t, b, Tokenizer.StartProperty.class, Tokenizer.State.PROPERTY, Tokenizer.State.STRING_LITERAL);
        advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, "baz");
        advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, "gorch");
        advanceNextTokenAndCheck(t, b, Tokenizer.EndProperty.class, Tokenizer.State.OBJECT, Tokenizer.State.OBJECT);
        advanceNextTokenAndCheck(t, b, Tokenizer.EndObject.class, Tokenizer.State.ROOT, Tokenizer.State.ROOT);
    }
}
