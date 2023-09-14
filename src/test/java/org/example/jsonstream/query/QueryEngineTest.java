package org.example.jsonstream.query;

import org.example.jsonstream.parser.Parser;
import org.example.jsonstream.tokenizer.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

class QueryEngineTest {
    
    @Test
    void QueryEngine_Basic () {
        
        CharBuffer buffer = new CharBuffer("{\"foo\":10,\"baz\":[true,10,{\"gorch\":35},[100,false]],\"bar\":3.14}");
        Tokenizer tokenizer = new Tokenizer(buffer);
        
        Parser parser = new Parser();
        
        try {
            QueryEngine.ObjectQuery objectQuery = new QueryEngine.ObjectQuery();
            
            objectQuery
                .getValueFor("foo")
                .getValueFor("baz")
                .getValueFor("bar");
            
            objectQuery.execute(tokenizer);
            
            System.out.println(objectQuery.getResults().get("foo").toString());
            System.out.println(objectQuery.getResults().get("bar").toString());
            System.out.println(objectQuery.getResults().get("baz").toString());
            
            objectQuery.getResults().get("baz").forEach(parser::consumeToken);
            
            System.out.println(parser.getRoot().toJSON());
            
        } catch (QueryEngine.QueryException e) {
            System.out.println(e);
        }
    }
    
    @Test
    void QueryEngine_WithStreamsAndStuff () {
        
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
            QueryEngine.ObjectQuery objectQuery = new QueryEngine.ObjectQuery();
            
            objectQuery
                .getValueFor("foo")
                .getValueFor("baz")
                .getValueFor("bar");
            
            objectQuery.execute(TokenProducer.of(tokens));
            
            System.out.println(objectQuery.getResults().get("foo").toString());
            System.out.println(objectQuery.getResults().get("bar").toString());
            System.out.println(objectQuery.getResults().get("baz").toString());
            
            objectQuery.getResults().get("baz").forEach(parser::consumeToken);
            
            System.out.println(parser.getRoot().toJSON());
            
        } catch (QueryEngine.QueryException e) {
            System.out.println(e);
        }
    }
    
}