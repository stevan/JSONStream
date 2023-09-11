package org.example.jsonstream.parser;

import org.example.jsonstream.tokenizer.CharBuffer;
import org.example.jsonstream.tokenizer.Tokenizer;
import org.example.jsonstream.tokenizer.Tokens;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
    
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