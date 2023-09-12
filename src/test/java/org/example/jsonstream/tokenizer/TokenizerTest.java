package org.example.jsonstream.tokenizer;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

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
        assertTrue(t.isInErrorState());
        
        ErrorToken err = (ErrorToken) token;
        assertEquals(err.getMsg(), "The root node must be either an Object({}) or an Array([])");
    }

    @Test
    void produceToken_NoToken() {
        CharBuffer b = new CharBuffer("");
        Tokenizer t = new Tokenizer(b);

        Token token = t.produceToken();
        
        assertInstanceOf(NoToken.class, token);
        assertTrue(t.isInEndState());
    }
    
    @Test
    void produceToken_StartArrayToken() {
        CharBuffer b = new CharBuffer("[");
        Tokenizer t = new Tokenizer(b);
        
        checkNextToken(t, StartArray.class);

        checkNextToken(t, ErrorToken.class);
        assertTrue(t.isInErrorState());
    }
    
    
    @Test
    void produceToken_StartAndEndOArrayToken() {
        CharBuffer b = new CharBuffer("[]");
        Tokenizer t = new Tokenizer(b);
        
        checkNextToken(t, StartArray.class);
        checkNextToken(t, EndArray.class);

        checkNextToken(t, NoToken.class);
        assertTrue(t.isInEndState());
    }
    
    @Test
    void produceToken_ArrayOrArrayToken() {
        CharBuffer b = new CharBuffer("[[]]");
        Tokenizer t = new Tokenizer(b);
        
        checkNextToken(t, StartArray.class);
            checkNextToken(t, StartItem.class);
                checkNextToken(t, StartArray.class);
                checkNextToken(t, EndArray.class);
            checkNextToken(t, EndItem.class);
        checkNextToken(t, EndArray.class);

        checkNextToken(t, NoToken.class);
        assertTrue(t.isInEndState());
    }
    
    @Test
    void produceToken_ArrayTokenWithInt() {
        CharBuffer b = new CharBuffer("[10]");
        Tokenizer t = new Tokenizer(b);
        
        checkNextToken(t, StartArray.class);
            checkNextToken(t, StartItem.class);
                checkAddIntToken(t, 10);
            checkNextToken(t, EndItem.class);
        checkNextToken(t, EndArray.class);

        checkNextToken(t, NoToken.class);
        assertTrue(t.isInEndState());
    }
    
    @Test
    void produceToken_ArrayTokenWithLiterals() {
        CharBuffer b = new CharBuffer("[10, 3.14, \"foo\"]");
        Tokenizer t = new Tokenizer(b);
        
        checkNextToken(t, StartArray.class);
            checkNextToken(t, StartItem.class);
                checkAddIntToken(t, 10);
            checkNextToken(t, EndItem.class);
            checkNextToken(t, StartItem.class);
                checkAddFloatToken(t, 3.14F);
            checkNextToken(t, EndItem.class);
            checkNextToken(t, StartItem.class);
                checkAddStringToken(t, "foo");
            checkNextToken(t, EndItem.class);
        checkNextToken(t, EndArray.class);

        checkNextToken(t, NoToken.class);
        assertTrue(t.isInEndState());
    }
    
    @Test
    void produceToken_ArrayTokenWithArraysLiterals() {
        CharBuffer b = new CharBuffer("[10, [3.14]]");
        Tokenizer t = new Tokenizer(b);
        
        checkNextToken(t, StartArray.class);
            checkNextToken(t, StartItem.class);
                checkAddIntToken(t, 10);
            checkNextToken(t, EndItem.class);
            checkNextToken(t, StartItem.class);
                checkNextToken(t, StartArray.class);
                    checkNextToken(t, StartItem.class);
                        checkAddFloatToken(t, 3.14F);
                    checkNextToken(t, EndItem.class);
                checkNextToken(t, EndArray.class);
            checkNextToken(t, EndItem.class);
        checkNextToken(t, EndArray.class);

        checkNextToken(t, NoToken.class);
        assertTrue(t.isInEndState());
    }

    @Test
    void produceToken_StartObjectToken() {
        CharBuffer b = new CharBuffer("{");
        Tokenizer t = new Tokenizer(b);

        checkNextToken(t, StartObject.class);

        checkNextToken(t, ErrorToken.class);
        assertTrue(t.isInErrorState());
    }


    @Test
    void produceToken_StartAndEndObjectToken() {
        CharBuffer b = new CharBuffer("{}");
        Tokenizer t = new Tokenizer(b);

        checkNextToken(t, StartObject.class);
        checkNextToken(t, EndObject.class);

        checkNextToken(t, NoToken.class);
        assertTrue(t.isInEndState());
    }

    @Test
    void produceToken_StartAndEndObjectTokenWithKey() {
        CharBuffer b = new CharBuffer("{\"foo\"");
        Tokenizer t = new Tokenizer(b);

        checkNextToken(t, StartObject.class);
            checkNextToken(t, StartProperty.class);
                checkAddKeyToken(t, "foo");

        checkNextToken(t, ErrorToken.class);
        assertTrue(t.isInErrorState());
    }
    
    @Test
    void produceToken_ObjectTokensWithSingleProperty() {
        CharBuffer b = new CharBuffer("{\"foo\":\"bar\"}");
        Tokenizer t = new Tokenizer(b);

        checkNextToken(t, StartObject.class);
            checkNextToken(t, StartProperty.class);
                checkAddKeyToken(t, "foo");
                checkAddStringToken(t, "bar");
            checkNextToken(t, EndProperty.class);
        checkNextToken(t, EndObject.class);

        checkNextToken(t, NoToken.class);
        assertTrue(t.isInEndState());
    }
    
    @Test
    void produceToken_ObjectTokensWithSingleNumericProperty() {
        CharBuffer b = new CharBuffer("{\"foo\":10}");
        Tokenizer t = new Tokenizer(b);
        
        checkNextToken(t, StartObject.class);
            checkNextToken(t, StartProperty.class);
                checkAddKeyToken(t, "foo");
                checkAddIntToken(t, 10);
            checkNextToken(t, EndProperty.class);
        checkNextToken(t, EndObject.class);

        checkNextToken(t, NoToken.class);
        assertTrue(t.isInEndState());
    }
    
    @Test
    void produceToken_ObjectTokensWithSingleFalseProperty() {
        CharBuffer b = new CharBuffer("{\"foo\":false}");
        Tokenizer t = new Tokenizer(b);
        
        checkNextToken(t, StartObject.class);
            checkNextToken(t, StartProperty.class);
                checkAddKeyToken(t, "foo");
                checkNextToken(t, AddFalse.class);
            checkNextToken(t, EndProperty.class);
        checkNextToken(t, EndObject.class);

        checkNextToken(t, NoToken.class);
        assertTrue(t.isInEndState());
    }
    
    @Test
    void produceToken_ObjectTokensWithSingleTrueProperty() {
        CharBuffer b = new CharBuffer("{\"foo\":true}");
        Tokenizer t = new Tokenizer(b);
        
        checkNextToken(t, StartObject.class);
            checkNextToken(t, StartProperty.class);
                checkAddKeyToken(t, "foo");
                checkNextToken(t, AddTrue.class);
            checkNextToken(t, EndProperty.class);
        checkNextToken(t, EndObject.class);

        checkNextToken(t, NoToken.class);
        assertTrue(t.isInEndState());
    }
    
    @Test
    void produceToken_ObjectTokensWithSingleNullProperty() {
        CharBuffer b = new CharBuffer("{\"foo\":null}");
        Tokenizer t = new Tokenizer(b);
        
        checkNextToken(t, StartObject.class);
            checkNextToken(t, StartProperty.class);
                checkAddKeyToken(t, "foo");
                checkNextToken(t, AddNull.class);
            checkNextToken(t, EndProperty.class);
        checkNextToken(t, EndObject.class);

        checkNextToken(t, NoToken.class);
        assertTrue(t.isInEndState());
    }
    
    @Test
    void produceToken_ObjectTokensWithSinglePropertyAndComplexValue() {
        CharBuffer b = new CharBuffer("{\"foo\":{}}");
        Tokenizer t = new Tokenizer(b);

        checkNextToken(t, StartObject.class);
            checkNextToken(t, StartProperty.class);
                checkAddKeyToken(t, "foo");
                checkNextToken(t, StartObject.class);
                checkNextToken(t, EndObject.class);
            checkNextToken(t, EndProperty.class);
        checkNextToken(t, EndObject.class);

        checkNextToken(t, NoToken.class);
        assertTrue(t.isInEndState());
    }
    
    @Test
    void produceToken_ObjectTokensWithTwoProperties() {
        CharBuffer b = new CharBuffer("{\"foo\":\"bar\",\"baz\":\"gorch\"}");
        Tokenizer t = new Tokenizer(b);
        
        checkNextToken(t, StartObject.class);
            checkNextToken(t, StartProperty.class);
                checkAddKeyToken(t, "foo");
                checkAddStringToken(t, "bar");
            checkNextToken(t, EndProperty.class);
            checkNextToken(t, StartProperty.class);
                checkAddKeyToken(t, "baz");
                checkAddStringToken(t, "gorch");
            checkNextToken(t, EndProperty.class);
        checkNextToken(t, EndObject.class);

        checkNextToken(t, NoToken.class);
        assertTrue(t.isInEndState());
    }
    
    @Test
    void produceToken_ObjectTokensWithTwoPropertiesWithDifferentTypes() {
        CharBuffer b = new CharBuffer("{\"foo\":\"bar\",\"baz\":3.14}");
        Tokenizer t = new Tokenizer(b);
        
        checkNextToken(t, StartObject.class);
            checkNextToken(t, StartProperty.class);
                checkAddKeyToken(t, "foo");
                checkAddStringToken(t, "bar");
            checkNextToken(t, EndProperty.class);
            checkNextToken(t, StartProperty.class);
                checkAddKeyToken(t, "baz");
                checkAddFloatToken(t, 3.14F);
            checkNextToken(t, EndProperty.class);
        checkNextToken(t, EndObject.class);

        checkNextToken(t, NoToken.class);
        assertTrue(t.isInEndState());
    }
    
    @Test
    void produceToken_AllTheThings() {
        CharBuffer b = new CharBuffer("[10, [3.14, {}, true],{\"foo\":true}, null, [false]]");
        Tokenizer t = new Tokenizer(b);
        
        checkNextToken(t, StartArray.class);
            checkNextToken(t, StartItem.class);
                checkAddIntToken(t, 10);
            checkNextToken(t, EndItem.class);
            checkNextToken(t, StartItem.class);
                checkNextToken(t, StartArray.class);
                    checkNextToken(t, StartItem.class);
                        checkAddFloatToken(t, 3.14F);
                    checkNextToken(t, EndItem.class);
                    checkNextToken(t, StartItem.class);
                        checkNextToken(t, StartObject.class);
                        checkNextToken(t, EndObject.class);
                    checkNextToken(t, EndItem.class);
                    checkNextToken(t, StartItem.class);
                        checkNextToken(t, AddTrue.class);
                    checkNextToken(t, EndItem.class);
                checkNextToken(t, EndArray.class);
            checkNextToken(t, EndItem.class);
            checkNextToken(t, StartItem.class);
                checkNextToken(t, StartObject.class);
                    checkNextToken(t, StartProperty.class);
                        checkAddKeyToken(t, "foo");
                        checkNextToken(t, AddTrue.class);
                    checkNextToken(t, EndProperty.class);
                checkNextToken(t, EndObject.class);
            checkNextToken(t, EndItem.class);
            checkNextToken(t, StartItem.class);
                checkNextToken(t, AddNull.class);
            checkNextToken(t, EndItem.class);
            checkNextToken(t, StartItem.class);
                checkNextToken(t, StartArray.class);
                    checkNextToken(t, StartItem.class);
                        checkNextToken(t, AddFalse.class);
                    checkNextToken(t, EndItem.class);
                checkNextToken(t, EndArray.class);
            checkNextToken(t, EndItem.class);
        checkNextToken(t, EndArray.class);

        checkNextToken(t, NoToken.class);
        assertTrue(t.isInEndState());
    }
    
    private void checkNextToken(Tokenizer tokenizer, Class<? extends Token> tokenClass) {
        Token token = tokenizer.produceToken();
        debugTokenizer(tokenizer, token);
        assertInstanceOf(tokenClass, token);
    }
    
    private void checkAddKeyToken(Tokenizer tokenizer, String expected_str) {
        Token token = tokenizer.produceToken();
        debugTokenizer(tokenizer, token);
        assertInstanceOf(AddKey.class, token);
        
        AddKey str = (AddKey) token;
        assertEquals(expected_str, str.getValue());
    }
    
    private void checkAddStringToken(Tokenizer tokenizer, String expected_str) {
        Token token = tokenizer.produceToken();
        debugTokenizer(tokenizer, token);
        assertInstanceOf(AddString.class, token);
        
        AddString str = (AddString) token;
        assertEquals(expected_str, str.getValue());
    }
    
    private void checkAddIntToken(Tokenizer tokenizer, Integer expected_int) {
        Token token = tokenizer.produceToken();
        debugTokenizer(tokenizer, token);
        assertInstanceOf(AddInt.class, token);
        
        AddInt i = (AddInt) token;
        assertEquals(expected_int, i.getValue());
    }
    
    private void checkAddFloatToken(Tokenizer tokenizer, Float expected_float) {
        Token token = tokenizer.produceToken();
        debugTokenizer(tokenizer, token);
        assertInstanceOf(AddFloat.class, token);
        
        AddFloat f = (AddFloat) token;
        assertEquals(expected_float, f.getValue());
    }
    
    private static void debugTokenizer(Tokenizer tokenizer, Token token) {
        System.out.println(tokenizer.getBuffer().toString() + " = " + token.toString() + " >> " + Arrays.toString(token.getContext()));
    }
}
