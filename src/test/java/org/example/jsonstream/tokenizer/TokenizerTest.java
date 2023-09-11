package org.example.jsonstream.tokenizer;

import org.junit.jupiter.api.Test;

import static org.example.jsonstream.tokenizer.Tokenizer.*;
import static org.example.jsonstream.tokenizer.Tokens.*;

import static org.junit.jupiter.api.Assertions.*;

class TokenizerTest {

    @Test
    void produceToken_ErrorToken() {
        CharBuffer b = new CharBuffer("1");
        Tokenizer t = new Tokenizer(b);

        Token token = t.produceToken();
        assertInstanceOf(ErrorToken.class, token);
        ErrorToken err = (ErrorToken) token;
        assertEquals(err.getMsg(), "The root node must be either an Object({}) or an Array([])");

        assertEquals(t.stack.peek(), State.ROOT);
        assertEquals(t.nextState, State.ERROR);
    }

    @Test
    void produceToken_NoToken() {
        CharBuffer b = new CharBuffer("");
        Tokenizer t = new Tokenizer(b);

        Token token = t.produceToken();
        assertInstanceOf(NoToken.class, token);

        assertEquals(t.stack.peek(), State.ROOT);
        assertEquals(t.nextState, State.END);
    }
    
    @Test
    void produceToken_StartArrayToken() {
        CharBuffer b = new CharBuffer("[");
        Tokenizer t = new Tokenizer(b);
        
        checkNextToken(t, StartArray.class, State.ARRAY, State.ITEM);

        checkNextToken(t, ErrorToken.class, State.ARRAY, State.ERROR);
    }
    
    
    @Test
    void produceToken_StartAndEndOArrayToken() {
        CharBuffer b = new CharBuffer("[]");
        Tokenizer t = new Tokenizer(b);
        
        checkNextToken(t, StartArray.class, State.ARRAY, State.ITEM);
        checkNextToken(t, EndArray.class, State.ROOT, State.ROOT);

        checkNextToken(t, NoToken.class, State.ROOT, State.END);
    }
    
    @Test
    void produceToken_ArrayOrArrayToken() {
        CharBuffer b = new CharBuffer("[[]]");
        Tokenizer t = new Tokenizer(b);
        
        checkNextToken(t, StartArray.class, State.ARRAY, State.ITEM);
            checkNextToken(t, StartItem.class, State.END_ITEM, State.ITEM);
                checkNextToken(t, StartArray.class, State.ARRAY, State.ITEM);
                checkNextToken(t, EndArray.class, State.END_ITEM, State.END_ITEM);
            checkNextToken(t, EndItem.class, State.ARRAY, State.ARRAY);
        checkNextToken(t, EndArray.class, State.ROOT, State.ROOT);

        checkNextToken(t, NoToken.class, State.ROOT, State.END);
    }
    
    @Test
    void produceToken_ArrayTokenWithInt() {
        CharBuffer b = new CharBuffer("[10]");
        Tokenizer t = new Tokenizer(b);
        
        checkNextToken(t, StartArray.class, State.ARRAY, State.ITEM);
            checkNextToken(t, StartItem.class, State.END_ITEM, State.ITEM);
                checkAddIntToken(t, 10, State.END_ITEM, State.END_ITEM);
            checkNextToken(t, EndItem.class, State.ARRAY, State.ARRAY);
        checkNextToken(t, EndArray.class, State.ROOT, State.ROOT);

        checkNextToken(t, NoToken.class, State.ROOT, State.END);
    }
    
    @Test
    void produceToken_ArrayTokenWithLiterals() {
        CharBuffer b = new CharBuffer("[10, 3.14, \"foo\"]");
        Tokenizer t = new Tokenizer(b);
        
        checkNextToken(t, StartArray.class, State.ARRAY, State.ITEM);
            checkNextToken(t, StartItem.class, State.END_ITEM, State.ITEM);
                checkAddIntToken(t, 10, State.END_ITEM, State.END_ITEM);
            checkNextToken(t, EndItem.class, State.ARRAY, State.ARRAY);
            checkNextToken(t, StartItem.class, State.END_ITEM, State.ITEM);
                checkAddFloatToken(t, 3.14F, State.END_ITEM, State.END_ITEM);
            checkNextToken(t, EndItem.class, State.ARRAY, State.ARRAY);
            checkNextToken(t, StartItem.class, State.END_ITEM, State.ITEM);
                checkAddStringToken(t, "foo", State.END_ITEM, State.END_ITEM);
            checkNextToken(t, EndItem.class, State.ARRAY, State.ARRAY);
        checkNextToken(t, EndArray.class, State.ROOT, State.ROOT);

        checkNextToken(t, NoToken.class, State.ROOT, State.END);
    }
    
    @Test
    void produceToken_ArrayTokenWithArraysLiterals() {
        CharBuffer b = new CharBuffer("[10, [3.14]]");
        Tokenizer t = new Tokenizer(b);
        
        checkNextToken(t, StartArray.class, State.ARRAY, State.ITEM);
            checkNextToken(t, StartItem.class, State.END_ITEM, State.ITEM);
                checkAddIntToken(t, 10, State.END_ITEM, State.END_ITEM);
            checkNextToken(t, EndItem.class, State.ARRAY, State.ARRAY);
            checkNextToken(t, StartItem.class, State.END_ITEM, State.ITEM);
                checkNextToken(t, StartArray.class, State.ARRAY, State.ITEM);
                    checkNextToken(t, StartItem.class, State.END_ITEM, State.ITEM);
                        checkAddFloatToken(t, 3.14F, State.END_ITEM, State.END_ITEM);
                    checkNextToken(t, EndItem.class, State.ARRAY, State.ARRAY);
                checkNextToken(t, EndArray.class, State.END_ITEM, State.END_ITEM);
            checkNextToken(t, EndItem.class, State.ARRAY, State.ARRAY);
        checkNextToken(t, EndArray.class, State.ROOT, State.ROOT);

        checkNextToken(t, NoToken.class, State.ROOT, State.END);
    }

    @Test
    void produceToken_StartObjectToken() {
        CharBuffer b = new CharBuffer("{");
        Tokenizer t = new Tokenizer(b);

        checkNextToken(t, StartObject.class, State.OBJECT, State.PROPERTY);

        checkNextToken(t, ErrorToken.class, State.OBJECT, State.ERROR);
    }


    @Test
    void produceToken_StartAndEndObjectToken() {
        CharBuffer b = new CharBuffer("{}");
        Tokenizer t = new Tokenizer(b);

        checkNextToken(t, StartObject.class, State.OBJECT, State.PROPERTY);
        checkNextToken(t, EndObject.class, State.ROOT, State.ROOT);

        checkNextToken(t, NoToken.class, State.ROOT, State.END);
    }

    @Test
    void produceToken_StartAndEndObjectTokenWithKey() {
        CharBuffer b = new CharBuffer("{\"foo\"");
        Tokenizer t = new Tokenizer(b);

        checkNextToken(t, StartObject.class, State.OBJECT, State.PROPERTY);
            checkNextToken(t, StartProperty.class, State.PROPERTY, State.KEY_LITERAL);
                checkAddKeyToken(t, "foo", State.PROPERTY, State.PROPERTY);

        checkNextToken(t, ErrorToken.class, State.PROPERTY, State.ERROR);
    }
    
    @Test
    void produceToken_ObjectTokensWithSingleProperty() {
        CharBuffer b = new CharBuffer("{\"foo\":\"bar\"}");
        Tokenizer t = new Tokenizer(b);

        checkNextToken(t, StartObject.class, State.OBJECT, State.PROPERTY);
            checkNextToken(t, StartProperty.class, State.PROPERTY, State.KEY_LITERAL);
                checkAddKeyToken(t, "foo", State.PROPERTY, State.PROPERTY);
                checkAddStringToken(t, "bar", State.PROPERTY, State.PROPERTY);
            checkNextToken(t, EndProperty.class, State.OBJECT, State.OBJECT);
        checkNextToken(t, EndObject.class, State.ROOT, State.ROOT);

        checkNextToken(t, NoToken.class, State.ROOT, State.END);
    }
    
    @Test
    void produceToken_ObjectTokensWithSingleNumericProperty() {
        CharBuffer b = new CharBuffer("{\"foo\":10}");
        Tokenizer t = new Tokenizer(b);
        
        checkNextToken(t, StartObject.class, State.OBJECT, State.PROPERTY);
            checkNextToken(t, StartProperty.class, State.PROPERTY, State.KEY_LITERAL);
                checkAddKeyToken(t, "foo", State.PROPERTY, State.PROPERTY);
                checkAddIntToken(t, 10, State.PROPERTY, State.PROPERTY);
            checkNextToken(t, EndProperty.class, State.OBJECT, State.OBJECT);
        checkNextToken(t, EndObject.class, State.ROOT, State.ROOT);

        checkNextToken(t, NoToken.class, State.ROOT, State.END);
    }
    
    @Test
    void produceToken_ObjectTokensWithSingleFalseProperty() {
        CharBuffer b = new CharBuffer("{\"foo\":false}");
        Tokenizer t = new Tokenizer(b);
        
        checkNextToken(t, StartObject.class, State.OBJECT, State.PROPERTY);
            checkNextToken(t, StartProperty.class, State.PROPERTY, State.KEY_LITERAL);
                checkAddKeyToken(t, "foo", State.PROPERTY, State.PROPERTY);
                checkNextToken(t, AddFalse.class, State.PROPERTY, State.PROPERTY);
            checkNextToken(t, EndProperty.class, State.OBJECT, State.OBJECT);
        checkNextToken(t, EndObject.class, State.ROOT, State.ROOT);

        checkNextToken(t, NoToken.class, State.ROOT, State.END);
    }
    
    @Test
    void produceToken_ObjectTokensWithSingleTrueProperty() {
        CharBuffer b = new CharBuffer("{\"foo\":true}");
        Tokenizer t = new Tokenizer(b);
        
        checkNextToken(t, StartObject.class, State.OBJECT, State.PROPERTY);
            checkNextToken(t, StartProperty.class, State.PROPERTY, State.KEY_LITERAL);
                checkAddKeyToken(t, "foo", State.PROPERTY, State.PROPERTY);
                checkNextToken(t, AddTrue.class, State.PROPERTY, State.PROPERTY);
            checkNextToken(t, EndProperty.class, State.OBJECT, State.OBJECT);
        checkNextToken(t, EndObject.class, State.ROOT, State.ROOT);

        checkNextToken(t, NoToken.class, State.ROOT, State.END);
    }
    
    @Test
    void produceToken_ObjectTokensWithSingleNullProperty() {
        CharBuffer b = new CharBuffer("{\"foo\":null}");
        Tokenizer t = new Tokenizer(b);
        
        checkNextToken(t, StartObject.class, State.OBJECT, State.PROPERTY);
            checkNextToken(t, StartProperty.class, State.PROPERTY, State.KEY_LITERAL);
                checkAddKeyToken(t, "foo", State.PROPERTY, State.PROPERTY);
                checkNextToken(t, AddNull.class, State.PROPERTY, State.PROPERTY);
            checkNextToken(t, EndProperty.class, State.OBJECT, State.OBJECT);
        checkNextToken(t, EndObject.class, State.ROOT, State.ROOT);

        checkNextToken(t, NoToken.class, State.ROOT, State.END);
    }
    
    @Test
    void produceToken_ObjectTokensWithSinglePropertyAndComplexValue() {
        CharBuffer b = new CharBuffer("{\"foo\":{}}");
        Tokenizer t = new Tokenizer(b);

        checkNextToken(t, StartObject.class, State.OBJECT, State.PROPERTY);
            checkNextToken(t, StartProperty.class, State.PROPERTY, State.KEY_LITERAL);
                checkAddKeyToken(t, "foo", State.PROPERTY, State.PROPERTY);
                checkNextToken(t, StartObject.class, State.OBJECT, State.PROPERTY);
                checkNextToken(t, EndObject.class, State.PROPERTY, State.PROPERTY);
            checkNextToken(t, EndProperty.class, State.OBJECT, State.OBJECT);
        checkNextToken(t, EndObject.class, State.ROOT, State.ROOT);

        checkNextToken(t, NoToken.class, State.ROOT, State.END);
    }
    
    @Test
    void produceToken_ObjectTokensWithTwoProperties() {
        CharBuffer b = new CharBuffer("{\"foo\":\"bar\",\"baz\":\"gorch\"}");
        Tokenizer t = new Tokenizer(b);
        
        checkNextToken(t, StartObject.class, State.OBJECT, State.PROPERTY);
            checkNextToken(t, StartProperty.class, State.PROPERTY, State.KEY_LITERAL);
                checkAddKeyToken(t, "foo", State.PROPERTY, State.PROPERTY);
                checkAddStringToken(t, "bar", State.PROPERTY, State.PROPERTY);
            checkNextToken(t, EndProperty.class, State.OBJECT, State.OBJECT);
            checkNextToken(t, StartProperty.class, State.PROPERTY, State.KEY_LITERAL);
                checkAddKeyToken(t, "baz", State.PROPERTY, State.PROPERTY);
                checkAddStringToken(t, "gorch", State.PROPERTY, State.PROPERTY);
            checkNextToken(t, EndProperty.class, State.OBJECT, State.OBJECT);
        checkNextToken(t, EndObject.class, State.ROOT, State.ROOT);

        checkNextToken(t, NoToken.class, State.ROOT, State.END);
    }
    
    @Test
    void produceToken_ObjectTokensWithTwoPropertiesWithDifferentTypes() {
        CharBuffer b = new CharBuffer("{\"foo\":\"bar\",\"baz\":3.14}");
        Tokenizer t = new Tokenizer(b);
        
        checkNextToken(t, StartObject.class, State.OBJECT, State.PROPERTY);
            checkNextToken(t, StartProperty.class, State.PROPERTY, State.KEY_LITERAL);
                checkAddKeyToken(t, "foo", State.PROPERTY, State.PROPERTY);
                checkAddStringToken(t, "bar", State.PROPERTY, State.PROPERTY);
            checkNextToken(t, EndProperty.class, State.OBJECT, State.OBJECT);
            checkNextToken(t, StartProperty.class, State.PROPERTY, State.KEY_LITERAL);
                checkAddKeyToken(t, "baz", State.PROPERTY, State.PROPERTY);
                checkAddFloatToken(t, 3.14F, State.PROPERTY, State.PROPERTY);
            checkNextToken(t, EndProperty.class, State.OBJECT, State.OBJECT);
        checkNextToken(t, EndObject.class, State.ROOT, State.ROOT);

        checkNextToken(t, NoToken.class, State.ROOT, State.END);
    }
    
    @Test
    void produceToken_AllTheThings() {
        CharBuffer b = new CharBuffer("[10, [3.14, {}, true],{\"foo\":true}, null, [false]]");
        Tokenizer t = new Tokenizer(b);
        
        checkNextToken(t, StartArray.class, State.ARRAY, State.ITEM);
            checkNextToken(t, StartItem.class, State.END_ITEM, State.ITEM);
                checkAddIntToken(t, 10, State.END_ITEM, State.END_ITEM);
            checkNextToken(t, EndItem.class, State.ARRAY, State.ARRAY);
            checkNextToken(t, StartItem.class, State.END_ITEM, State.ITEM);
                checkNextToken(t, StartArray.class, State.ARRAY, State.ITEM);
                    checkNextToken(t, StartItem.class, State.END_ITEM, State.ITEM);
                        checkAddFloatToken(t, 3.14F, State.END_ITEM, State.END_ITEM);
                    checkNextToken(t, EndItem.class, State.ARRAY, State.ARRAY);
                    checkNextToken(t, StartItem.class, State.END_ITEM, State.ITEM);
                        checkNextToken(t, StartObject.class, State.OBJECT, State.PROPERTY);
                        checkNextToken(t, EndObject.class, State.END_ITEM, State.END_ITEM);
                    checkNextToken(t, EndItem.class, State.ARRAY, State.ARRAY);
                    checkNextToken(t, StartItem.class, State.END_ITEM, State.ITEM);
                        checkNextToken(t, AddTrue.class, State.END_ITEM, State.END_ITEM);
                    checkNextToken(t, EndItem.class, State.ARRAY, State.ARRAY);
                checkNextToken(t, EndArray.class, State.END_ITEM, State.END_ITEM);
            checkNextToken(t, EndItem.class, State.ARRAY, State.ARRAY);
            checkNextToken(t, StartItem.class, State.END_ITEM, State.ITEM);
                checkNextToken(t, StartObject.class, State.OBJECT, State.PROPERTY);
                    checkNextToken(t, StartProperty.class, State.PROPERTY, State.KEY_LITERAL);
                        checkAddKeyToken(t, "foo", State.PROPERTY, State.PROPERTY);
                        checkNextToken(t, AddTrue.class, State.PROPERTY, State.PROPERTY);
                    checkNextToken(t, EndProperty.class, State.OBJECT, State.OBJECT);
                checkNextToken(t, EndObject.class, State.END_ITEM, State.END_ITEM);
            checkNextToken(t, EndItem.class, State.ARRAY, State.ARRAY);
            checkNextToken(t, StartItem.class, State.END_ITEM, State.ITEM);
                checkNextToken(t, AddNull.class, State.END_ITEM, State.END_ITEM);
            checkNextToken(t, EndItem.class, State.ARRAY, State.ARRAY);
            checkNextToken(t, StartItem.class, State.END_ITEM, State.ITEM);
                checkNextToken(t, StartArray.class, State.ARRAY, State.ITEM);
                    checkNextToken(t, StartItem.class, State.END_ITEM, State.ITEM);
                        checkNextToken(t, AddFalse.class, State.END_ITEM, State.END_ITEM);
                    checkNextToken(t, EndItem.class, State.ARRAY, State.ARRAY);
                checkNextToken(t, EndArray.class, State.END_ITEM, State.END_ITEM);
            checkNextToken(t, EndItem.class, State.ARRAY, State.ARRAY);
        checkNextToken(t, EndArray.class, State.ROOT, State.ROOT);

        checkNextToken(t, NoToken.class, State.ROOT, State.END);
    }
    
    private static void debugTokenizer(Tokenizer tokenizer, Token token) {
        //System.out.println(tokenizer.buffer.toString() + " = " + token.toString() + " -> " + tokenizer.nextState);
        //System.out.println("CONTEXT : " + tokenizer.context.toString());
    }
    
    private void checkNextToken(Tokenizer tokenizer, Class<? extends Token> tokenClass, State peek, State nextState) {
        Token token = tokenizer.produceToken();
        debugTokenizer(tokenizer, token);
        assertInstanceOf(tokenClass, token);
        
        assertEquals(peek, tokenizer.stack.peek());
        assertEquals(nextState, tokenizer.nextState);
    }
    
    private void checkAddKeyToken(Tokenizer tokenizer, String expected_str, State peek, State nextState) {
        Token token = tokenizer.produceToken();
        debugTokenizer(tokenizer, token);
        assertInstanceOf(AddKey.class, token);
        
        AddKey str = (AddKey) token;
        assertEquals(expected_str, str.getValue());
        
        assertEquals(peek, tokenizer.stack.peek());
        assertEquals(nextState, tokenizer.nextState);
    }
    
    private void checkAddStringToken(Tokenizer tokenizer, String expected_str, State peek, State nextState) {
        Token token = tokenizer.produceToken();
        debugTokenizer(tokenizer, token);
        assertInstanceOf(AddString.class, token);
        
        AddString str = (AddString) token;
        assertEquals(expected_str, str.getValue());
        
        assertEquals(peek, tokenizer.stack.peek());
        assertEquals(nextState, tokenizer.nextState);
    }
    
    private void checkAddIntToken(Tokenizer tokenizer, Integer expected_int, State peek, State nextState) {
        Token token = tokenizer.produceToken();
        debugTokenizer(tokenizer, token);
        assertInstanceOf(AddInt.class, token);
        
        AddInt i = (AddInt) token;
        assertEquals(expected_int, i.getValue());
        
        assertEquals(peek, tokenizer.stack.peek());
        assertEquals(nextState, tokenizer.nextState);
    }
    
    private void checkAddFloatToken(Tokenizer tokenizer, Float expected_float, State peek, State nextState) {
        Token token = tokenizer.produceToken();
        debugTokenizer(tokenizer, token);
        assertInstanceOf(AddFloat.class, token);
        
        AddFloat f = (AddFloat) token;
        assertEquals(expected_float, f.getValue());
        
        assertEquals(peek, tokenizer.stack.peek());
        assertEquals(nextState, tokenizer.nextState);
    }
}
