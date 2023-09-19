package org.example.jsonstream.tokenizer;

import org.junit.jupiter.api.Test;

import static org.example.jsonstream.tokenizer.Tokens.*;

import static org.junit.jupiter.api.Assertions.*;

class TokenizerTest {

    @Test
    void produceToken_ErrorToken() {
        Tokenizer t = new Tokenizer(new Scanner("1"));

        Token token = t.produceToken();
        assertInstanceOf(ErrorToken.class, token);
        assertTrue(t.isInErrorState());
        
        ErrorToken err = (ErrorToken) token;
        assertEquals("The root node must be either an Object({}) or an Array([])", err.getMsg());
        
        assertTrue(t.isDone());
    }

    @Test
    void produceToken_NoToken() {
        Tokenizer t = new Tokenizer(new Scanner(""));

        Token token = t.produceToken();
        
        assertInstanceOf(NoToken.class, token);
        assertTrue(t.isInEndState());
        
        assertTrue(t.isDone());
    }
    
    @Test
    void produceToken_StartArrayToken() {
        Tokenizer t = new Tokenizer(new Scanner("["));
        
        checkNextToken(t, StartArray.class);
        assertFalse(t.isDone());

        checkNextToken(t, ErrorToken.class);
        assertTrue(t.isInErrorState());
        
        // and it will keep returning error
        checkNextToken(t, ErrorToken.class);
        assertTrue(t.isInErrorState());
        
        assertTrue(t.isDone());
    }
    
    
    @Test
    void produceToken_StartAndEndOArrayToken() {
        Tokenizer t = new Tokenizer(new Scanner("[]"));
        
        checkNextToken(t, StartArray.class);
        assertFalse(t.isDone());
        checkNextToken(t, EndArray.class);

        checkNextToken(t, NoToken.class);
        assertTrue(t.isInEndState());
        
        // and it will return no token forever now
        checkNextToken(t, NoToken.class);
        assertTrue(t.isInEndState());
        
        assertTrue(t.isDone());
    }
    
    @Test
    void produceToken_ArrayOrArrayToken() {
        Tokenizer t = new Tokenizer(new Scanner("[[]]"));
        
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
        Tokenizer t = new Tokenizer(new Scanner("[10]"));
        
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
        Tokenizer t = new Tokenizer(new Scanner("[10, 3.14, \"foo\"]"));
        
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
        Tokenizer t = new Tokenizer(new Scanner("[10, [3.14]]"));
        
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
        Tokenizer t = new Tokenizer(new Scanner("{"));

        checkNextToken(t, StartObject.class);

        checkNextToken(t, ErrorToken.class);
        assertTrue(t.isInErrorState());
    }


    @Test
    void produceToken_StartAndEndObjectToken() {
        Tokenizer t = new Tokenizer(new Scanner("{}"));

        checkNextToken(t, StartObject.class);
        checkNextToken(t, EndObject.class);

        checkNextToken(t, NoToken.class);
        assertTrue(t.isInEndState());
    }

    @Test
    void produceToken_StartAndEndObjectTokenWithKey() {
        Tokenizer t = new Tokenizer(new Scanner("{\"foo\""));

        checkNextToken(t, StartObject.class);
            checkNextToken(t, StartProperty.class);
                checkAddKeyToken(t, "foo");

        checkNextToken(t, ErrorToken.class);
        assertTrue(t.isInErrorState());
    }
    
    @Test
    void produceToken_ObjectTokensWithSingleProperty() {
        Tokenizer t = new Tokenizer(new Scanner("{\"foo\":\"bar\"}"));

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
        Tokenizer t = new Tokenizer(new Scanner("{\"foo\":10}"));
        
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
        Tokenizer t = new Tokenizer(new Scanner("{\"foo\":false}"));
        
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
        Tokenizer t = new Tokenizer(new Scanner("{\"foo\":true}"));
        
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
        Tokenizer t = new Tokenizer(new Scanner("{\"foo\":null}"));
        
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
        Tokenizer t = new Tokenizer(new Scanner("{\"foo\":{}}"));

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
        Tokenizer t = new Tokenizer(new Scanner("{\"foo\":\"bar\",\"baz\":\"gorch\"}"));
        
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
        Tokenizer t = new Tokenizer(new Scanner("{\"foo\":\"bar\",\"baz\":3.14}"));
        
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
        Tokenizer t = new Tokenizer(new Scanner("[10, [3.14, {}, true],{\"foo\":true}, null, [false]]"));
        
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
        //System.out.println(tokenizer.getBuffer().toString() + " = " + token.toString() + " >> " + Arrays.toString(token.getContext()));
    }
}
