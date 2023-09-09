package org.example.jsonstream;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TokenizerTest {

    private void advanceNextTokenAndCheck(Tokenizer tokenizer, CharBuffer buffer, Class<? extends Tokens.Token> tokenClass, Tokenizer.State peek, Tokenizer.State nextState) {
        Tokens.Token token = tokenizer.produceToken(buffer);
        //System.out.println(token.get().toString() + " -> " + tokenizer.state.toString() + " : " + tokenizer.nextState);
        assertInstanceOf(tokenClass, token);

        assertEquals(peek, tokenizer.state.peek());
        assertEquals(nextState, tokenizer.nextState);
    }

    private void advanceNextTokenAndCheckAddString(Tokenizer tokenizer, CharBuffer buffer, Tokenizer.State peek, Tokenizer.State nextState, String expected_str) {
        Tokens.Token token = tokenizer.produceToken(buffer);
        assertInstanceOf(Tokens.AddString.class, token);

        Tokens.AddString str = (Tokens.AddString) token;
        assertEquals(expected_str, str.getValue());

        assertEquals(peek, tokenizer.state.peek());
        assertEquals(nextState, tokenizer.nextState);
    }
    
    private void advanceNextTokenAndCheckAddInt(Tokenizer tokenizer, CharBuffer buffer, Tokenizer.State peek, Tokenizer.State nextState, Integer expected_int) {
        Tokens.Token token = tokenizer.produceToken(buffer);
        //System.out.println(token.toString() + " -> " + tokenizer.state.toString() + " : " + tokenizer.nextState);
        assertInstanceOf(Tokens.AddInt.class, token);
        
        Tokens.AddInt i = (Tokens.AddInt) token;
        assertEquals(expected_int, i.getValue());
        
        assertEquals(peek, tokenizer.state.peek());
        assertEquals(nextState, tokenizer.nextState);
    }
    
    private void advanceNextTokenAndCheckAddFloat(Tokenizer tokenizer, CharBuffer buffer, Tokenizer.State peek, Tokenizer.State nextState, Float expected_float) {
        Tokens.Token token = tokenizer.produceToken(buffer);
        assertInstanceOf(Tokens.AddFloat.class, token);
        
        Tokens.AddFloat f = (Tokens.AddFloat) token;
        assertEquals(expected_float, f.getValue());
        
        assertEquals(peek, tokenizer.state.peek());
        assertEquals(nextState, tokenizer.nextState);
    }

    @Test
    void produceToken_ErrorToken() {
        CharBuffer b = new CharBuffer("1");
        Tokenizer t = new Tokenizer();

        Tokens.Token token = t.produceToken(b);
        assertInstanceOf(Tokens.ErrorToken.class, token);
        Tokens.ErrorToken err = (Tokens.ErrorToken) token;
        assertEquals(err.getMsg(), "The root node must be either an Object({}) or an Array([])");

        // TODO: this should probaby be the ERROR state
        assertEquals(t.state.peek(), Tokenizer.State.ROOT);
    }

    @Test
    void produceToken_NoToken() {
        CharBuffer b = new CharBuffer("");
        Tokenizer t = new Tokenizer();

        Tokens.Token token = t.produceToken(b);
        assertInstanceOf(Tokens.NoToken.class, token);

        assertEquals(t.state.peek(), Tokenizer.State.ROOT);
    }

    @Test
    void produceToken_StartObjectToken() {
        CharBuffer b = new CharBuffer("{");
        Tokenizer t = new Tokenizer();

        advanceNextTokenAndCheck(t, b, Tokens.StartObject.class, Tokenizer.State.OBJECT, Tokenizer.State.PROPERTY);
    }


    @Test
    void produceToken_StartAndEndObjectToken() {
        CharBuffer b = new CharBuffer("{}");
        Tokenizer t = new Tokenizer();

        advanceNextTokenAndCheck(t, b, Tokens.StartObject.class, Tokenizer.State.OBJECT, Tokenizer.State.PROPERTY);
        advanceNextTokenAndCheck(t, b, Tokens.EndObject.class, Tokenizer.State.ROOT, Tokenizer.State.ROOT);
    }

    @Test
    void produceToken_StartAndEndObjectTokenWithKey() {
        CharBuffer b = new CharBuffer("{\"foo\"");
        Tokenizer t = new Tokenizer();

        advanceNextTokenAndCheck(t, b, Tokens.StartObject.class, Tokenizer.State.OBJECT, Tokenizer.State.PROPERTY);
        advanceNextTokenAndCheck(t, b, Tokens.StartProperty.class, Tokenizer.State.PROPERTY, Tokenizer.State.STRING_LITERAL);
        advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, "foo");
    }
    
    @Test
    void produceToken_ObjectTokensWithSingleProperty() {
        CharBuffer b = new CharBuffer("{\"foo\":\"bar\"}");
        Tokenizer t = new Tokenizer();

        advanceNextTokenAndCheck(t, b, Tokens.StartObject.class, Tokenizer.State.OBJECT, Tokenizer.State.PROPERTY);
        advanceNextTokenAndCheck(t, b, Tokens.StartProperty.class, Tokenizer.State.PROPERTY, Tokenizer.State.STRING_LITERAL);
        advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, "foo");
        advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, "bar");
        advanceNextTokenAndCheck(t, b, Tokens.EndProperty.class, Tokenizer.State.OBJECT, Tokenizer.State.OBJECT);
        advanceNextTokenAndCheck(t, b, Tokens.EndObject.class, Tokenizer.State.ROOT, Tokenizer.State.ROOT);
    }
    
    @Test
    void produceToken_ObjectTokensWithSingleNumericProperty() {
        CharBuffer b = new CharBuffer("{\"foo\":10}");
        Tokenizer t = new Tokenizer();
        
        advanceNextTokenAndCheck(t, b, Tokens.StartObject.class, Tokenizer.State.OBJECT, Tokenizer.State.PROPERTY);
        advanceNextTokenAndCheck(t, b, Tokens.StartProperty.class, Tokenizer.State.PROPERTY, Tokenizer.State.STRING_LITERAL);
        advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, "foo");
        advanceNextTokenAndCheckAddInt(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, 10);
        advanceNextTokenAndCheck(t, b, Tokens.EndProperty.class, Tokenizer.State.OBJECT, Tokenizer.State.OBJECT);
        advanceNextTokenAndCheck(t, b, Tokens.EndObject.class, Tokenizer.State.ROOT, Tokenizer.State.ROOT);
    }
    
    @Test
    void produceToken_ObjectTokensWithSinglePropertyAndComplexValue() {
        CharBuffer b = new CharBuffer("{\"foo\":{}}");
        Tokenizer t = new Tokenizer();

        advanceNextTokenAndCheck(t, b, Tokens.StartObject.class, Tokenizer.State.OBJECT, Tokenizer.State.PROPERTY);
        advanceNextTokenAndCheck(t, b, Tokens.StartProperty.class, Tokenizer.State.PROPERTY, Tokenizer.State.STRING_LITERAL);
        advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, "foo");
        advanceNextTokenAndCheck(t, b, Tokens.StartObject.class, Tokenizer.State.OBJECT, Tokenizer.State.PROPERTY);
        advanceNextTokenAndCheck(t, b, Tokens.EndObject.class, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY);
        advanceNextTokenAndCheck(t, b, Tokens.EndProperty.class, Tokenizer.State.OBJECT, Tokenizer.State.OBJECT);
        advanceNextTokenAndCheck(t, b, Tokens.EndObject.class, Tokenizer.State.ROOT, Tokenizer.State.ROOT);
    }
    
    @Test
    void produceToken_ObjectTokensWithTwoProperties() {
        CharBuffer b = new CharBuffer("{\"foo\":\"bar\",\"baz\":\"gorch\"}");
        Tokenizer t = new Tokenizer();
        
        advanceNextTokenAndCheck(t, b, Tokens.StartObject.class, Tokenizer.State.OBJECT, Tokenizer.State.PROPERTY);
        advanceNextTokenAndCheck(t, b, Tokens.StartProperty.class, Tokenizer.State.PROPERTY, Tokenizer.State.STRING_LITERAL);
        advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, "foo");
        advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, "bar");
        advanceNextTokenAndCheck(t, b, Tokens.EndProperty.class, Tokenizer.State.OBJECT, Tokenizer.State.OBJECT);
        advanceNextTokenAndCheck(t, b, Tokens.StartProperty.class, Tokenizer.State.PROPERTY, Tokenizer.State.STRING_LITERAL);
        advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, "baz");
        advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, "gorch");
        advanceNextTokenAndCheck(t, b, Tokens.EndProperty.class, Tokenizer.State.OBJECT, Tokenizer.State.OBJECT);
        advanceNextTokenAndCheck(t, b, Tokens.EndObject.class, Tokenizer.State.ROOT, Tokenizer.State.ROOT);
    }
}
