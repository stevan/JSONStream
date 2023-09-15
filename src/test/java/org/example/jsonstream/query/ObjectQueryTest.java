package org.example.jsonstream.query;

import org.example.jsonstream.parser.Parser;
import org.example.jsonstream.tokenizer.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

class ObjectQueryTest {
    
    @Test
    void ObjectQuery_Basic () {
        
        CharBuffer buffer = new CharBuffer("{\"foo\":10,\"baz\":[true,10,{\"gorch\":35},[100,false]],\"bar\":3.14}");
        Tokenizer tokenizer = new Tokenizer(buffer);
        
        Parser parser = new Parser();
        
        try {
            ObjectQuery objectQuery = new ObjectQuery();
            
            objectQuery
                .captureValueOf("foo")
                .captureValueOf("baz")
                .captureValueOf("bar");
            
            objectQuery.execute(tokenizer);
            
            System.out.println(objectQuery.getResultsFor("foo").toString());
            System.out.println(objectQuery.getResultsFor("bar").toString());
            System.out.println(objectQuery.getResultsFor("baz").toString());
            
            objectQuery.getResultsFor("baz").forEach(parser::consumeToken);
            
            System.out.println(parser.getRoot().toJSON());
            
        } catch (QueryException e) {
            System.out.println(e);
        }
    }
    
    @Test
    void ObjectQuery_WithTokenProducer () {
        
        CharBuffer buffer = new CharBuffer("{\"foo\":10,\"baz\":[true,10,{\"gorch\":35},[100,false]],\"bar\":3.14}");
        Tokenizer tokenizer = new Tokenizer(buffer);
        
        List<Tokens.Token> tokens = tokenizer.stream().collect(Collectors.toList());
        
        Parser parser = new Parser();
        
        //System.out.println(tokens.toString());
        //System.out.println("----------------------------------------");
        //TokenProducer.of(tokens).stream().forEach(System.out::println);
        //System.out.println("----------------------------------------");
        //TokenProducer.of(tokens).stream().forEach(System.out::println);
        //System.out.println("----------------------------------------");
        
        try {
            ObjectQuery objectQuery = new ObjectQuery();
            
            objectQuery
                .captureValueOf("foo")
                .captureValueOf("baz")
                .captureValueOf("bar");
            
            objectQuery.execute(TokenProducer.of(tokens));
            
            System.out.println(objectQuery.getResultsFor("foo").toString());
            System.out.println(objectQuery.getResultsFor("bar").toString());
            System.out.println(objectQuery.getResultsFor("baz").toString());
            
            objectQuery.getResultsFor("baz").forEach(parser::consumeToken);
            
            System.out.println(parser.getRoot().toJSON());
            
        } catch (QueryException e) {
            System.out.println(e);
        }
    }
    
}