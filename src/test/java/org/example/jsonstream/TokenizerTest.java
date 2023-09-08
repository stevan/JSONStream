package org.example.jsonstream;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TokenizerTest {

    @Test
    void produceToken_ErrorToken() {
        CharBuffer buffer = new CharBuffer("1");
        Tokenizer tokenizer = new Tokenizer();
        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = tokenizer.produceToken(buffer);
            assertTrue(token.isPresent());
            assertInstanceOf(Tokenizer.ErrorToken.class, token.get());
            Tokenizer.ErrorToken t = (Tokenizer.ErrorToken) token.get();
            assertEquals(t.getMsg(), "The root node must be either an Object({}) or an Array([])");
        });

        // TODO: this should probaby be the ERROR state
        assertSame(tokenizer.state.peek(), Tokenizer.State.ROOT);
    }

    @Test
    void produceToken_NoToken() {
        CharBuffer buffer = new CharBuffer("");
        Tokenizer tokenizer = new Tokenizer();
        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = tokenizer.produceToken(buffer);
            assertTrue(token.isPresent());
            assertInstanceOf(Tokenizer.NoToken.class, token.get());
        });

        assertSame(tokenizer.state.peek(), Tokenizer.State.ROOT);
    }

    @Test
    void produceToken_StartObjectToken() {
        CharBuffer buffer = new CharBuffer("{");
        Tokenizer tokenizer = new Tokenizer();
        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = tokenizer.produceToken(buffer);
            assertTrue(token.isPresent());
            assertInstanceOf(Tokenizer.StartObject.class, token.get());
        });

        assertSame(tokenizer.state.peek(), Tokenizer.State.OBJECT);
        assertSame(tokenizer.nextState, Tokenizer.State.PROPERTY);
    }

    @Test
    void produceToken_StartAndEndObjectToken() {
        CharBuffer buffer = new CharBuffer("{}");
        Tokenizer tokenizer = new Tokenizer();

        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = tokenizer.produceToken(buffer);
            assertTrue(token.isPresent());
            assertInstanceOf(Tokenizer.StartObject.class, token.get());
        });

        assertSame(tokenizer.state.peek(), Tokenizer.State.OBJECT);
        assertSame(tokenizer.nextState, Tokenizer.State.PROPERTY);

        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = tokenizer.produceToken(buffer);
            assertTrue(token.isPresent());
            assertInstanceOf(Tokenizer.EndObject.class, token.get());
        });
        
        assertSame(tokenizer.state.peek(), Tokenizer.State.ROOT);
        assertSame(tokenizer.nextState, Tokenizer.State.ROOT);
    }

    @Test
    void produceToken_StartAndEndObjectTokenWithKey() {
        CharBuffer buffer = new CharBuffer("{\"foo\"");
        Tokenizer tokenizer = new Tokenizer();

        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = tokenizer.produceToken(buffer);
            assertTrue(token.isPresent());
            assertInstanceOf(Tokenizer.StartObject.class, token.get());
        });

        assertSame(tokenizer.state.peek(), Tokenizer.State.OBJECT);
        assertSame(tokenizer.nextState, Tokenizer.State.PROPERTY);

        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = tokenizer.produceToken(buffer);
            assertTrue(token.isPresent());
            assertInstanceOf(Tokenizer.StartProperty.class, token.get());
        });

        assertSame(tokenizer.state.peek(), Tokenizer.State.PROPERTY);
        assertSame(tokenizer.nextState, Tokenizer.State.STRING_LITERAL);

        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = tokenizer.produceToken(buffer);
            assertTrue(token.isPresent());
            assertInstanceOf(Tokenizer.AddString.class, token.get());

            Tokenizer.AddString str = (Tokenizer.AddString) token.get();
            assertEquals(str.getValue(), "foo");
        });

        assertSame(tokenizer.state.peek(), Tokenizer.State.PROPERTY);
        assertSame(tokenizer.nextState, Tokenizer.State.PROPERTY);
    }
    
    @Test
    void produceToken_ObjectTokensWithSingleProperty() {
        CharBuffer buffer = new CharBuffer("{\"foo\":\"bar\"}");
        Tokenizer tokenizer = new Tokenizer();
        
        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = tokenizer.produceToken(buffer);
            assertTrue(token.isPresent());
            System.out.println(token.get().toString() + " -> " + tokenizer.state.toString());
            assertInstanceOf(Tokenizer.StartObject.class, token.get());
        });
        
        assertSame(tokenizer.state.peek(), Tokenizer.State.OBJECT);
        assertSame(tokenizer.nextState, Tokenizer.State.PROPERTY);
        
        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = tokenizer.produceToken(buffer);
            assertTrue(token.isPresent());
            System.out.println(token.get().toString() + " -> " + tokenizer.state.toString());
            assertInstanceOf(Tokenizer.StartProperty.class, token.get());
        });
        
        assertSame(tokenizer.state.peek(), Tokenizer.State.PROPERTY);
        assertSame(tokenizer.nextState, Tokenizer.State.STRING_LITERAL);
        
        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = tokenizer.produceToken(buffer);
            assertTrue(token.isPresent());
            System.out.println(token.get().toString() + " -> " + tokenizer.state.toString());
            assertInstanceOf(Tokenizer.AddString.class, token.get());
            
            Tokenizer.AddString str = (Tokenizer.AddString) token.get();
            assertEquals(str.getValue(), "foo");
        });
        
        assertSame(tokenizer.state.peek(), Tokenizer.State.PROPERTY);
        assertSame(tokenizer.nextState, Tokenizer.State.PROPERTY);
        
        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = tokenizer.produceToken(buffer);
            assertTrue(token.isPresent());
            System.out.println(token.get().toString() + " -> " + tokenizer.state.toString());
            assertInstanceOf(Tokenizer.AddString.class, token.get());
            
            Tokenizer.AddString str = (Tokenizer.AddString) token.get();
            assertEquals(str.getValue(), "bar");
        });
        
        assertSame(tokenizer.state.peek(), Tokenizer.State.PROPERTY);
        assertSame(tokenizer.nextState, Tokenizer.State.PROPERTY);
        
        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = tokenizer.produceToken(buffer);
            assertTrue(token.isPresent());
            System.out.println(token.get().toString() + " -> " + tokenizer.state.toString());
            assertInstanceOf(Tokenizer.EndProperty.class, token.get());
        });
        
        assertSame(tokenizer.state.peek(), Tokenizer.State.OBJECT);
        assertSame(tokenizer.nextState, Tokenizer.State.OBJECT);
        
        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = tokenizer.produceToken(buffer);
            assertTrue(token.isPresent());
            System.out.println(token.get().toString() + " -> " + tokenizer.state.toString());
            assertInstanceOf(Tokenizer.EndObject.class, token.get());
        });
        
        assertSame(tokenizer.state.peek(), Tokenizer.State.ROOT);
        assertSame(tokenizer.nextState, Tokenizer.State.ROOT);
    }
    
    @Test
    void produceToken_ObjectTokensWithSinglePropertyAndComplexValue() {
        CharBuffer buffer = new CharBuffer("{\"foo\":{}}");
        Tokenizer tokenizer = new Tokenizer();
        
        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = tokenizer.produceToken(buffer);
            assertTrue(token.isPresent());
            System.out.println(token.get().toString() + " -> " + tokenizer.state.toString());
            assertInstanceOf(Tokenizer.StartObject.class, token.get());
        });
        
        assertSame(tokenizer.state.peek(), Tokenizer.State.OBJECT);
        assertSame(tokenizer.nextState, Tokenizer.State.PROPERTY);
        
        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = tokenizer.produceToken(buffer);
            assertTrue(token.isPresent());
            System.out.println(token.get().toString() + " -> " + tokenizer.state.toString());
            assertInstanceOf(Tokenizer.StartProperty.class, token.get());
        });
        
        assertSame(tokenizer.state.peek(), Tokenizer.State.PROPERTY);
        assertSame(tokenizer.nextState, Tokenizer.State.STRING_LITERAL);
        
        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = tokenizer.produceToken(buffer);
            assertTrue(token.isPresent());
            System.out.println(token.get().toString() + " -> " + tokenizer.state.toString());
            assertInstanceOf(Tokenizer.AddString.class, token.get());
            
            Tokenizer.AddString str = (Tokenizer.AddString) token.get();
            assertEquals(str.getValue(), "foo");
        });
        
        assertSame(tokenizer.state.peek(), Tokenizer.State.PROPERTY);
        assertSame(tokenizer.nextState, Tokenizer.State.PROPERTY);
        
        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = tokenizer.produceToken(buffer);
            assertTrue(token.isPresent());
            System.out.println(token.get().toString() + " -> " + tokenizer.state.toString());
            assertInstanceOf(Tokenizer.StartObject.class, token.get());
        });
        
        assertSame(tokenizer.state.peek(), Tokenizer.State.OBJECT);
        assertSame(tokenizer.nextState, Tokenizer.State.PROPERTY);
        
        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = tokenizer.produceToken(buffer);
            assertTrue(token.isPresent());
            System.out.println(token.get().toString() + " -> " + tokenizer.state.toString());
            assertInstanceOf(Tokenizer.EndObject.class, token.get());
        });
        
        assertSame(tokenizer.state.peek(), Tokenizer.State.PROPERTY);
        assertSame(tokenizer.nextState, Tokenizer.State.PROPERTY);
        
        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = tokenizer.produceToken(buffer);
            assertTrue(token.isPresent());
            System.out.println(token.get().toString() + " -> " + tokenizer.state.toString());
            assertInstanceOf(Tokenizer.EndProperty.class, token.get());
        });
        
        assertSame(tokenizer.state.peek(), Tokenizer.State.OBJECT);
        assertSame(tokenizer.nextState, Tokenizer.State.OBJECT);
        
        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = tokenizer.produceToken(buffer);
            assertTrue(token.isPresent());
            System.out.println(token.get().toString() + " -> " + tokenizer.state.toString());
            assertInstanceOf(Tokenizer.EndObject.class, token.get());
        });
        
        assertSame(tokenizer.state.peek(), Tokenizer.State.ROOT);
        assertSame(tokenizer.nextState, Tokenizer.State.ROOT);
    }
    
    @Test
    void produceToken_ObjectTokensWithTwoProperties() {
        CharBuffer buffer = new CharBuffer("{\"foo\":\"bar\",\"baz\":\"gorch\"}");
        Tokenizer tokenizer = new Tokenizer();
        
        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = tokenizer.produceToken(buffer);
            assertTrue(token.isPresent());
            System.out.println(token.get().toString() + " -> " + tokenizer.state.toString());
            assertInstanceOf(Tokenizer.StartObject.class, token.get());
        });
        
        assertSame(tokenizer.state.peek(), Tokenizer.State.OBJECT);
        assertSame(tokenizer.nextState, Tokenizer.State.PROPERTY);
        
        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = tokenizer.produceToken(buffer);
            assertTrue(token.isPresent());
            System.out.println(token.get().toString() + " -> " + tokenizer.state.toString());
            assertInstanceOf(Tokenizer.StartProperty.class, token.get());
        });
        
        assertSame(tokenizer.state.peek(), Tokenizer.State.PROPERTY);
        assertSame(tokenizer.nextState, Tokenizer.State.STRING_LITERAL);
        
        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = tokenizer.produceToken(buffer);
            assertTrue(token.isPresent());
            System.out.println(token.get().toString() + " -> " + tokenizer.state.toString() + " : " + tokenizer.nextState);
            assertInstanceOf(Tokenizer.AddString.class, token.get());
            
            Tokenizer.AddString str = (Tokenizer.AddString) token.get();
            assertEquals(str.getValue(), "foo");
        });
        
        assertSame(tokenizer.state.peek(), Tokenizer.State.PROPERTY);
        assertSame(tokenizer.nextState, Tokenizer.State.PROPERTY);
        
        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = tokenizer.produceToken(buffer);
            assertTrue(token.isPresent());
            System.out.println(token.get().toString() + " -> " + tokenizer.state.toString() + " : " + tokenizer.nextState);
            assertInstanceOf(Tokenizer.AddString.class, token.get());
            
            Tokenizer.AddString str = (Tokenizer.AddString) token.get();
            assertEquals(str.getValue(), "bar");
        });
        
        assertSame(tokenizer.state.peek(), Tokenizer.State.PROPERTY);
        assertSame(tokenizer.nextState, Tokenizer.State.PROPERTY);
        
        assertDoesNotThrow(() -> {
            System.out.println(buffer.toString());
            Optional<Tokenizer.Token> token = tokenizer.produceToken(buffer);
            System.out.println(buffer.toString());
            assertTrue(token.isPresent());
            System.out.println(token.get().toString() + " -> " + tokenizer.state.toString() + " : " + tokenizer.nextState);
            assertInstanceOf(Tokenizer.EndProperty.class, token.get());
        });
        
        assertSame(tokenizer.state.peek(), Tokenizer.State.OBJECT);
        assertSame(tokenizer.nextState, Tokenizer.State.OBJECT);
        
        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = tokenizer.produceToken(buffer);
            assertTrue(token.isPresent());
            System.out.println(token.get().toString() + " -> " + tokenizer.state.toString());
            assertInstanceOf(Tokenizer.StartProperty.class, token.get());
        });
        
        assertSame(tokenizer.state.peek(), Tokenizer.State.PROPERTY);
        assertSame(tokenizer.nextState, Tokenizer.State.STRING_LITERAL);
        
        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = tokenizer.produceToken(buffer);
            assertTrue(token.isPresent());
            System.out.println(token.get().toString() + " -> " + tokenizer.state.toString());
            assertInstanceOf(Tokenizer.AddString.class, token.get());
            
            Tokenizer.AddString str = (Tokenizer.AddString) token.get();
            assertEquals(str.getValue(), "baz");
        });
        
        assertSame(tokenizer.state.peek(), Tokenizer.State.PROPERTY);
        assertSame(tokenizer.nextState, Tokenizer.State.PROPERTY);
        
        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = tokenizer.produceToken(buffer);
            assertTrue(token.isPresent());
            System.out.println(token.get().toString() + " -> " + tokenizer.state.toString());
            assertInstanceOf(Tokenizer.AddString.class, token.get());
            
            Tokenizer.AddString str = (Tokenizer.AddString) token.get();
            assertEquals(str.getValue(), "gorch");
        });
        
        assertSame(tokenizer.state.peek(), Tokenizer.State.PROPERTY);
        assertSame(tokenizer.nextState, Tokenizer.State.PROPERTY);
        
        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = tokenizer.produceToken(buffer);
            assertTrue(token.isPresent());
            System.out.println(token.get().toString() + " -> " + tokenizer.state.toString());
            assertInstanceOf(Tokenizer.EndProperty.class, token.get());
        });
        
        assertSame(tokenizer.state.peek(), Tokenizer.State.OBJECT);
        assertSame(tokenizer.nextState, Tokenizer.State.OBJECT);
        
        assertDoesNotThrow(() -> {
            Optional<Tokenizer.Token> token = tokenizer.produceToken(buffer);
            assertTrue(token.isPresent());
            System.out.println(token.get().toString() + " -> " + tokenizer.state.toString());
            assertInstanceOf(Tokenizer.EndObject.class, token.get());
        });
        
        assertSame(tokenizer.state.peek(), Tokenizer.State.ROOT);
        assertSame(tokenizer.nextState, Tokenizer.State.ROOT);
    }
}