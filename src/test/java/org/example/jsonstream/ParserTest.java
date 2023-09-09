package org.example.jsonstream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
    
    @Test
    void consumeToken() {
        
        Parser p = new Parser();
        
        p.consumeToken(new Tokens.StartObject());
            p.consumeToken(new Tokens.StartProperty());
                p.consumeToken(new Tokens.AddKey("foo"));
                p.consumeToken(new Tokens.AddTrue());
            p.consumeToken(new Tokens.EndProperty());
            p.consumeToken(new Tokens.StartProperty());
                p.consumeToken(new Tokens.AddKey("bar"));
                p.consumeToken(new Tokens.StartArray());
                p.consumeToken(new Tokens.EndArray());
            p.consumeToken(new Tokens.EndProperty());
        p.consumeToken(new Tokens.EndObject());
        
        assertTrue(p.hasRoot());
        assertEquals("{\"foo\":true,\"bar\":[]}", p.getRoot().toJSON());
    }
    
    @Test
    void consumeTokensFromTokenizer() {
        final String expected = "{\"foo\":true,\"bar\":[10,3.14,{},null],\"baz\":false}";
        
        CharBuffer buffer = new CharBuffer(expected);
        Tokenizer tokenizer = new Tokenizer(buffer);
        Parser parser = new Parser();
        
        tokenizer.asStream().forEach(parser::consumeToken);
        
        assertTrue(parser.hasRoot());
        assertEquals(expected, parser.getRoot().toJSON());
        
    }
    
    @Test
    void consumeTokensFromTokenizerWithArrayRoot() {
        final String expected = "[100,1.332277,{\"foo\":true,\"bar\":[10,3.14,{},null],\"baz\":false},null,null,null]";
        
        CharBuffer buffer = new CharBuffer(expected);
        Tokenizer tokenizer = new Tokenizer(buffer);
        Parser parser = new Parser();
        
        tokenizer.asStream().forEach(parser::consumeToken);
        
        assertTrue(parser.hasRoot());
        assertEquals(expected, parser.getRoot().toJSON());
        
    }
}