package org.example.jsonstream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenizerTest {

    private void advanceNextTokenAndCheck(Tokenizer tokenizer, Class<? extends Tokens.Token> tokenClass, Tokenizer.State peek, Tokenizer.State nextState) {
        Tokens.Token token = tokenizer.produceToken();
        //System.out.println(buffer.toString() + " = " + token.toString() + " -> " + tokenizer.state.toString() + " : " + tokenizer.nextState);
        assertInstanceOf(tokenClass, token);

        assertEquals(peek, tokenizer.state.peek());
        assertEquals(nextState, tokenizer.nextState);
    }

    private void advanceNextTokenAndCheckAddString(Tokenizer tokenizer, CharBuffer buffer, Tokenizer.State peek, Tokenizer.State nextState, String expected_str) {
        Tokens.Token token = tokenizer.produceToken();
        //System.out.println(token.toString() + " -> " + tokenizer.state.toString() + " : " + tokenizer.nextState);
        assertInstanceOf(Tokens.AddString.class, token);

        Tokens.AddString str = (Tokens.AddString) token;
        assertEquals(expected_str, str.getValue());

        assertEquals(peek, tokenizer.state.peek());
        assertEquals(nextState, tokenizer.nextState);
    }
    
    private void advanceNextTokenAndCheckAddInt(Tokenizer tokenizer, Tokenizer.State peek, Tokenizer.State nextState, Integer expected_int) {
        Tokens.Token token = tokenizer.produceToken();
        //System.out.println(token.toString() + " -> " + tokenizer.state.toString() + " : " + tokenizer.nextState);
        assertInstanceOf(Tokens.AddInt.class, token);
        
        Tokens.AddInt i = (Tokens.AddInt) token;
        assertEquals(expected_int, i.getValue());
        
        assertEquals(peek, tokenizer.state.peek());
        assertEquals(nextState, tokenizer.nextState);
    }
    
    private void advanceNextTokenAndCheckAddFloat(Tokenizer tokenizer, Tokenizer.State peek, Tokenizer.State nextState, Float expected_float) {
        Tokens.Token token = tokenizer.produceToken();
        //System.out.println(token.toString() + " -> " + tokenizer.state.toString() + " : " + tokenizer.nextState);
        assertInstanceOf(Tokens.AddFloat.class, token);
        
        Tokens.AddFloat f = (Tokens.AddFloat) token;
        assertEquals(expected_float, f.getValue());
        
        assertEquals(peek, tokenizer.state.peek());
        assertEquals(nextState, tokenizer.nextState);
    }

    @Test
    void produceToken_ErrorToken() {
        CharBuffer b = new CharBuffer("1");
        Tokenizer t = new Tokenizer(b);

        Tokens.Token token = t.produceToken();
        assertInstanceOf(Tokens.ErrorToken.class, token);
        Tokens.ErrorToken err = (Tokens.ErrorToken) token;
        assertEquals(err.getMsg(), "The root node must be either an Object({}) or an Array([])");

        // TODO: this should probaby be the ERROR state
        assertEquals(t.state.peek(), Tokenizer.State.ROOT);
    }

    @Test
    void produceToken_NoToken() {
        CharBuffer b = new CharBuffer("");
        Tokenizer t = new Tokenizer(b);

        Tokens.Token token = t.produceToken();
        assertInstanceOf(Tokens.NoToken.class, token);

        assertEquals(t.state.peek(), Tokenizer.State.ROOT);
    }
    
    @Test
    void produceToken_StartArrayToken() {
        CharBuffer b = new CharBuffer("[");
        Tokenizer t = new Tokenizer(b);
        
        advanceNextTokenAndCheck(t, Tokens.StartArray.class, Tokenizer.State.ARRAY, Tokenizer.State.ITEM);
    }
    
    
    @Test
    void produceToken_StartAndEndOArrayToken() {
        CharBuffer b = new CharBuffer("[]");
        Tokenizer t = new Tokenizer(b);
        
        advanceNextTokenAndCheck(t, Tokens.StartArray.class, Tokenizer.State.ARRAY, Tokenizer.State.ITEM);
        advanceNextTokenAndCheck(t, Tokens.EndArray.class, Tokenizer.State.ROOT, Tokenizer.State.ROOT);
    }
    
    @Test
    void produceToken_ArrayOrArrayToken() {
        CharBuffer b = new CharBuffer("[[]]");
        Tokenizer t = new Tokenizer(b);
        
        advanceNextTokenAndCheck(t, Tokens.StartArray.class, Tokenizer.State.ARRAY, Tokenizer.State.ITEM);
            advanceNextTokenAndCheck(t, Tokens.StartItem.class, Tokenizer.State.END_ITEM, Tokenizer.State.ITEM);
                advanceNextTokenAndCheck(t, Tokens.StartArray.class, Tokenizer.State.ARRAY, Tokenizer.State.ITEM);
                advanceNextTokenAndCheck(t, Tokens.EndArray.class, Tokenizer.State.END_ITEM, Tokenizer.State.END_ITEM);
            advanceNextTokenAndCheck(t, Tokens.EndItem.class, Tokenizer.State.ARRAY, Tokenizer.State.ARRAY);
        advanceNextTokenAndCheck(t, Tokens.EndArray.class, Tokenizer.State.ROOT, Tokenizer.State.ROOT);
    }
    
    @Test
    void produceToken_ArrayTokenWithInt() {
        CharBuffer b = new CharBuffer("[10]");
        Tokenizer t = new Tokenizer(b);
        
        advanceNextTokenAndCheck(t, Tokens.StartArray.class, Tokenizer.State.ARRAY, Tokenizer.State.ITEM);
            advanceNextTokenAndCheck(t, Tokens.StartItem.class, Tokenizer.State.END_ITEM, Tokenizer.State.ITEM);
                advanceNextTokenAndCheckAddInt(t, Tokenizer.State.END_ITEM, Tokenizer.State.END_ITEM, 10);
            advanceNextTokenAndCheck(t, Tokens.EndItem.class, Tokenizer.State.ARRAY, Tokenizer.State.ARRAY);
        advanceNextTokenAndCheck(t, Tokens.EndArray.class, Tokenizer.State.ROOT, Tokenizer.State.ROOT);
    }
    
    @Test
    void produceToken_ArrayTokenWithLiterals() {
        CharBuffer b = new CharBuffer("[10, 3.14, \"foo\"]");
        Tokenizer t = new Tokenizer(b);
        
        advanceNextTokenAndCheck(t, Tokens.StartArray.class, Tokenizer.State.ARRAY, Tokenizer.State.ITEM);
            advanceNextTokenAndCheck(t, Tokens.StartItem.class, Tokenizer.State.END_ITEM, Tokenizer.State.ITEM);
                advanceNextTokenAndCheckAddInt(t, Tokenizer.State.END_ITEM, Tokenizer.State.END_ITEM, 10);
            advanceNextTokenAndCheck(t, Tokens.EndItem.class, Tokenizer.State.ARRAY, Tokenizer.State.ARRAY);
            advanceNextTokenAndCheck(t, Tokens.StartItem.class, Tokenizer.State.END_ITEM, Tokenizer.State.ITEM);
                advanceNextTokenAndCheckAddFloat(t, Tokenizer.State.END_ITEM, Tokenizer.State.END_ITEM, 3.14F);
            advanceNextTokenAndCheck(t, Tokens.EndItem.class, Tokenizer.State.ARRAY, Tokenizer.State.ARRAY);
            advanceNextTokenAndCheck(t, Tokens.StartItem.class, Tokenizer.State.END_ITEM, Tokenizer.State.ITEM);
                advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.END_ITEM, Tokenizer.State.END_ITEM, "foo");
            advanceNextTokenAndCheck(t, Tokens.EndItem.class, Tokenizer.State.ARRAY, Tokenizer.State.ARRAY);
        advanceNextTokenAndCheck(t, Tokens.EndArray.class, Tokenizer.State.ROOT, Tokenizer.State.ROOT);
    }
    
    @Test
    void produceToken_ArrayTokenWithArraysLiterals() {
        CharBuffer b = new CharBuffer("[10, [3.14]]");
        Tokenizer t = new Tokenizer(b);
        
        advanceNextTokenAndCheck(t, Tokens.StartArray.class, Tokenizer.State.ARRAY, Tokenizer.State.ITEM);
            advanceNextTokenAndCheck(t, Tokens.StartItem.class, Tokenizer.State.END_ITEM, Tokenizer.State.ITEM);
                advanceNextTokenAndCheckAddInt(t, Tokenizer.State.END_ITEM, Tokenizer.State.END_ITEM, 10);
            advanceNextTokenAndCheck(t, Tokens.EndItem.class, Tokenizer.State.ARRAY, Tokenizer.State.ARRAY);
            advanceNextTokenAndCheck(t, Tokens.StartItem.class, Tokenizer.State.END_ITEM, Tokenizer.State.ITEM);
                advanceNextTokenAndCheck(t, Tokens.StartArray.class, Tokenizer.State.ARRAY, Tokenizer.State.ITEM);
                    advanceNextTokenAndCheck(t, Tokens.StartItem.class, Tokenizer.State.END_ITEM, Tokenizer.State.ITEM);
                        advanceNextTokenAndCheckAddFloat(t, Tokenizer.State.END_ITEM, Tokenizer.State.END_ITEM, 3.14F);
                    advanceNextTokenAndCheck(t, Tokens.EndItem.class, Tokenizer.State.ARRAY, Tokenizer.State.ARRAY);
                advanceNextTokenAndCheck(t, Tokens.EndArray.class, Tokenizer.State.END_ITEM, Tokenizer.State.END_ITEM);
            advanceNextTokenAndCheck(t, Tokens.EndItem.class, Tokenizer.State.ARRAY, Tokenizer.State.ARRAY);
        advanceNextTokenAndCheck(t, Tokens.EndArray.class, Tokenizer.State.ROOT, Tokenizer.State.ROOT);
    }

    @Test
    void produceToken_StartObjectToken() {
        CharBuffer b = new CharBuffer("{");
        Tokenizer t = new Tokenizer(b);

        advanceNextTokenAndCheck(t, Tokens.StartObject.class, Tokenizer.State.OBJECT, Tokenizer.State.PROPERTY);
    }


    @Test
    void produceToken_StartAndEndObjectToken() {
        CharBuffer b = new CharBuffer("{}");
        Tokenizer t = new Tokenizer(b);

        advanceNextTokenAndCheck(t, Tokens.StartObject.class, Tokenizer.State.OBJECT, Tokenizer.State.PROPERTY);
        advanceNextTokenAndCheck(t, Tokens.EndObject.class, Tokenizer.State.ROOT, Tokenizer.State.ROOT);
    }

    @Test
    void produceToken_StartAndEndObjectTokenWithKey() {
        CharBuffer b = new CharBuffer("{\"foo\"");
        Tokenizer t = new Tokenizer(b);

        advanceNextTokenAndCheck(t, Tokens.StartObject.class, Tokenizer.State.OBJECT, Tokenizer.State.PROPERTY);
            advanceNextTokenAndCheck(t, Tokens.StartProperty.class, Tokenizer.State.PROPERTY, Tokenizer.State.STRING_LITERAL);
                advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, "foo");
    }
    
    @Test
    void produceToken_ObjectTokensWithSingleProperty() {
        CharBuffer b = new CharBuffer("{\"foo\":\"bar\"}");
        Tokenizer t = new Tokenizer(b);

        advanceNextTokenAndCheck(t, Tokens.StartObject.class, Tokenizer.State.OBJECT, Tokenizer.State.PROPERTY);
            advanceNextTokenAndCheck(t, Tokens.StartProperty.class, Tokenizer.State.PROPERTY, Tokenizer.State.STRING_LITERAL);
                advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, "foo");
                advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, "bar");
            advanceNextTokenAndCheck(t, Tokens.EndProperty.class, Tokenizer.State.OBJECT, Tokenizer.State.OBJECT);
        advanceNextTokenAndCheck(t, Tokens.EndObject.class, Tokenizer.State.ROOT, Tokenizer.State.ROOT);
    }
    
    @Test
    void produceToken_ObjectTokensWithSingleNumericProperty() {
        CharBuffer b = new CharBuffer("{\"foo\":10}");
        Tokenizer t = new Tokenizer(b);
        
        advanceNextTokenAndCheck(t, Tokens.StartObject.class, Tokenizer.State.OBJECT, Tokenizer.State.PROPERTY);
            advanceNextTokenAndCheck(t, Tokens.StartProperty.class, Tokenizer.State.PROPERTY, Tokenizer.State.STRING_LITERAL);
                advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, "foo");
                advanceNextTokenAndCheckAddInt(t, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, 10);
            advanceNextTokenAndCheck(t, Tokens.EndProperty.class, Tokenizer.State.OBJECT, Tokenizer.State.OBJECT);
        advanceNextTokenAndCheck(t, Tokens.EndObject.class, Tokenizer.State.ROOT, Tokenizer.State.ROOT);
    }
    
    @Test
    void produceToken_ObjectTokensWithSingleFalseProperty() {
        CharBuffer b = new CharBuffer("{\"foo\":false}");
        Tokenizer t = new Tokenizer(b);
        
        advanceNextTokenAndCheck(t, Tokens.StartObject.class, Tokenizer.State.OBJECT, Tokenizer.State.PROPERTY);
            advanceNextTokenAndCheck(t, Tokens.StartProperty.class, Tokenizer.State.PROPERTY, Tokenizer.State.STRING_LITERAL);
                advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, "foo");
                advanceNextTokenAndCheck(t, Tokens.AddFalse.class, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY);
            advanceNextTokenAndCheck(t, Tokens.EndProperty.class, Tokenizer.State.OBJECT, Tokenizer.State.OBJECT);
        advanceNextTokenAndCheck(t, Tokens.EndObject.class, Tokenizer.State.ROOT, Tokenizer.State.ROOT);
    }
    
    @Test
    void produceToken_ObjectTokensWithSingleTrueProperty() {
        CharBuffer b = new CharBuffer("{\"foo\":true}");
        Tokenizer t = new Tokenizer(b);
        
        advanceNextTokenAndCheck(t, Tokens.StartObject.class, Tokenizer.State.OBJECT, Tokenizer.State.PROPERTY);
            advanceNextTokenAndCheck(t, Tokens.StartProperty.class, Tokenizer.State.PROPERTY, Tokenizer.State.STRING_LITERAL);
                advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, "foo");
                advanceNextTokenAndCheck(t, Tokens.AddTrue.class, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY);
            advanceNextTokenAndCheck(t, Tokens.EndProperty.class, Tokenizer.State.OBJECT, Tokenizer.State.OBJECT);
        advanceNextTokenAndCheck(t, Tokens.EndObject.class, Tokenizer.State.ROOT, Tokenizer.State.ROOT);
    }
    
    @Test
    void produceToken_ObjectTokensWithSingleNullProperty() {
        CharBuffer b = new CharBuffer("{\"foo\":null}");
        Tokenizer t = new Tokenizer(b);
        
        advanceNextTokenAndCheck(t, Tokens.StartObject.class, Tokenizer.State.OBJECT, Tokenizer.State.PROPERTY);
            advanceNextTokenAndCheck(t, Tokens.StartProperty.class, Tokenizer.State.PROPERTY, Tokenizer.State.STRING_LITERAL);
                advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, "foo");
                advanceNextTokenAndCheck(t, Tokens.AddNull.class, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY);
            advanceNextTokenAndCheck(t, Tokens.EndProperty.class, Tokenizer.State.OBJECT, Tokenizer.State.OBJECT);
        advanceNextTokenAndCheck(t, Tokens.EndObject.class, Tokenizer.State.ROOT, Tokenizer.State.ROOT);
    }
    
    @Test
    void produceToken_ObjectTokensWithSinglePropertyAndComplexValue() {
        CharBuffer b = new CharBuffer("{\"foo\":{}}");
        Tokenizer t = new Tokenizer(b);

        advanceNextTokenAndCheck(t, Tokens.StartObject.class, Tokenizer.State.OBJECT, Tokenizer.State.PROPERTY);
            advanceNextTokenAndCheck(t, Tokens.StartProperty.class, Tokenizer.State.PROPERTY, Tokenizer.State.STRING_LITERAL);
                advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, "foo");
                advanceNextTokenAndCheck(t, Tokens.StartObject.class, Tokenizer.State.OBJECT, Tokenizer.State.PROPERTY);
                advanceNextTokenAndCheck(t, Tokens.EndObject.class, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY);
            advanceNextTokenAndCheck(t, Tokens.EndProperty.class, Tokenizer.State.OBJECT, Tokenizer.State.OBJECT);
        advanceNextTokenAndCheck(t, Tokens.EndObject.class, Tokenizer.State.ROOT, Tokenizer.State.ROOT);
    }
    
    @Test
    void produceToken_ObjectTokensWithTwoProperties() {
        CharBuffer b = new CharBuffer("{\"foo\":\"bar\",\"baz\":\"gorch\"}");
        Tokenizer t = new Tokenizer(b);
        
        advanceNextTokenAndCheck(t, Tokens.StartObject.class, Tokenizer.State.OBJECT, Tokenizer.State.PROPERTY);
            advanceNextTokenAndCheck(t, Tokens.StartProperty.class, Tokenizer.State.PROPERTY, Tokenizer.State.STRING_LITERAL);
                advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, "foo");
                advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, "bar");
            advanceNextTokenAndCheck(t, Tokens.EndProperty.class, Tokenizer.State.OBJECT, Tokenizer.State.OBJECT);
            advanceNextTokenAndCheck(t, Tokens.StartProperty.class, Tokenizer.State.PROPERTY, Tokenizer.State.STRING_LITERAL);
                advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, "baz");
                advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, "gorch");
            advanceNextTokenAndCheck(t, Tokens.EndProperty.class, Tokenizer.State.OBJECT, Tokenizer.State.OBJECT);
        advanceNextTokenAndCheck(t, Tokens.EndObject.class, Tokenizer.State.ROOT, Tokenizer.State.ROOT);
    }
    
    @Test
    void produceToken_ObjectTokensWithTwoPropertiesWithDifferentTypes() {
        CharBuffer b = new CharBuffer("{\"foo\":\"bar\",\"baz\":3.14}");
        Tokenizer t = new Tokenizer(b);
        
        advanceNextTokenAndCheck(t, Tokens.StartObject.class, Tokenizer.State.OBJECT, Tokenizer.State.PROPERTY);
            advanceNextTokenAndCheck(t, Tokens.StartProperty.class, Tokenizer.State.PROPERTY, Tokenizer.State.STRING_LITERAL);
                advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, "foo");
                advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, "bar");
            advanceNextTokenAndCheck(t, Tokens.EndProperty.class, Tokenizer.State.OBJECT, Tokenizer.State.OBJECT);
            advanceNextTokenAndCheck(t, Tokens.StartProperty.class, Tokenizer.State.PROPERTY, Tokenizer.State.STRING_LITERAL);
                advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, "baz");
                advanceNextTokenAndCheckAddFloat(t, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, 3.14F);
            advanceNextTokenAndCheck(t, Tokens.EndProperty.class, Tokenizer.State.OBJECT, Tokenizer.State.OBJECT);
        advanceNextTokenAndCheck(t, Tokens.EndObject.class, Tokenizer.State.ROOT, Tokenizer.State.ROOT);
    }
    
    @Test
    void produceToken_AllTheThings() {
        CharBuffer b = new CharBuffer("[10, [3.14, {}, true],{\"foo\":true}, null, [false]]");
        Tokenizer t = new Tokenizer(b);
        
        advanceNextTokenAndCheck(t, Tokens.StartArray.class, Tokenizer.State.ARRAY, Tokenizer.State.ITEM);
            advanceNextTokenAndCheck(t, Tokens.StartItem.class, Tokenizer.State.END_ITEM, Tokenizer.State.ITEM);
                advanceNextTokenAndCheckAddInt(t, Tokenizer.State.END_ITEM, Tokenizer.State.END_ITEM, 10);
            advanceNextTokenAndCheck(t, Tokens.EndItem.class, Tokenizer.State.ARRAY, Tokenizer.State.ARRAY);
            advanceNextTokenAndCheck(t, Tokens.StartItem.class, Tokenizer.State.END_ITEM, Tokenizer.State.ITEM);
                advanceNextTokenAndCheck(t, Tokens.StartArray.class, Tokenizer.State.ARRAY, Tokenizer.State.ITEM);
                    advanceNextTokenAndCheck(t, Tokens.StartItem.class, Tokenizer.State.END_ITEM, Tokenizer.State.ITEM);
                        advanceNextTokenAndCheckAddFloat(t, Tokenizer.State.END_ITEM, Tokenizer.State.END_ITEM, 3.14F);
                    advanceNextTokenAndCheck(t, Tokens.EndItem.class, Tokenizer.State.ARRAY, Tokenizer.State.ARRAY);
                    advanceNextTokenAndCheck(t, Tokens.StartItem.class, Tokenizer.State.END_ITEM, Tokenizer.State.ITEM);
                        advanceNextTokenAndCheck(t, Tokens.StartObject.class, Tokenizer.State.OBJECT, Tokenizer.State.PROPERTY);
                        advanceNextTokenAndCheck(t, Tokens.EndObject.class, Tokenizer.State.END_ITEM, Tokenizer.State.END_ITEM);
                    advanceNextTokenAndCheck(t, Tokens.EndItem.class, Tokenizer.State.ARRAY, Tokenizer.State.ARRAY);
                    advanceNextTokenAndCheck(t, Tokens.StartItem.class, Tokenizer.State.END_ITEM, Tokenizer.State.ITEM);
                        advanceNextTokenAndCheck(t, Tokens.AddTrue.class, Tokenizer.State.END_ITEM, Tokenizer.State.END_ITEM);
                    advanceNextTokenAndCheck(t, Tokens.EndItem.class, Tokenizer.State.ARRAY, Tokenizer.State.ARRAY);
                advanceNextTokenAndCheck(t, Tokens.EndArray.class, Tokenizer.State.END_ITEM, Tokenizer.State.END_ITEM);
            advanceNextTokenAndCheck(t, Tokens.EndItem.class, Tokenizer.State.ARRAY, Tokenizer.State.ARRAY);
            advanceNextTokenAndCheck(t, Tokens.StartItem.class, Tokenizer.State.END_ITEM, Tokenizer.State.ITEM);
                advanceNextTokenAndCheck(t, Tokens.StartObject.class, Tokenizer.State.OBJECT, Tokenizer.State.PROPERTY);
                    advanceNextTokenAndCheck(t, Tokens.StartProperty.class, Tokenizer.State.PROPERTY, Tokenizer.State.STRING_LITERAL);
                        advanceNextTokenAndCheckAddString(t, b, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY, "foo");
                        advanceNextTokenAndCheck(t, Tokens.AddTrue.class, Tokenizer.State.PROPERTY, Tokenizer.State.PROPERTY);
                    advanceNextTokenAndCheck(t, Tokens.EndProperty.class, Tokenizer.State.OBJECT, Tokenizer.State.OBJECT);
                advanceNextTokenAndCheck(t, Tokens.EndObject.class, Tokenizer.State.END_ITEM, Tokenizer.State.END_ITEM);
            advanceNextTokenAndCheck(t, Tokens.EndItem.class, Tokenizer.State.ARRAY, Tokenizer.State.ARRAY);
            advanceNextTokenAndCheck(t, Tokens.StartItem.class, Tokenizer.State.END_ITEM, Tokenizer.State.ITEM);
                advanceNextTokenAndCheck(t, Tokens.AddNull.class, Tokenizer.State.END_ITEM, Tokenizer.State.END_ITEM);
            advanceNextTokenAndCheck(t, Tokens.EndItem.class, Tokenizer.State.ARRAY, Tokenizer.State.ARRAY);
            advanceNextTokenAndCheck(t, Tokens.StartItem.class, Tokenizer.State.END_ITEM, Tokenizer.State.ITEM);
                advanceNextTokenAndCheck(t, Tokens.StartArray.class, Tokenizer.State.ARRAY, Tokenizer.State.ITEM);
                    advanceNextTokenAndCheck(t, Tokens.StartItem.class, Tokenizer.State.END_ITEM, Tokenizer.State.ITEM);
                        advanceNextTokenAndCheck(t, Tokens.AddFalse.class, Tokenizer.State.END_ITEM, Tokenizer.State.END_ITEM);
                    advanceNextTokenAndCheck(t, Tokens.EndItem.class, Tokenizer.State.ARRAY, Tokenizer.State.ARRAY);
                advanceNextTokenAndCheck(t, Tokens.EndArray.class, Tokenizer.State.END_ITEM, Tokenizer.State.END_ITEM);
            advanceNextTokenAndCheck(t, Tokens.EndItem.class, Tokenizer.State.ARRAY, Tokenizer.State.ARRAY);
        advanceNextTokenAndCheck(t, Tokens.EndArray.class, Tokenizer.State.ROOT, Tokenizer.State.ROOT);
    }
}
