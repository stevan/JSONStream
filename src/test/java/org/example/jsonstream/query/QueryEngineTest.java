package org.example.jsonstream.query;

import org.example.jsonstream.tokenizer.*;
import org.junit.jupiter.api.Test;

class QueryEngineTest {
    
    @Test
    void QueryEngine_Basic () {
        
        CharBuffer buffer = new CharBuffer("{\"foo\":10,\"baz\":[true,10,{\"gorch\":35},[100,false]],\"bar\":3.14}");
        Tokenizer tokenizer = new Tokenizer(buffer);
        
        try {
            QueryEngine.ObjectQuery objectQuery = new QueryEngine.ObjectQuery();
            
            objectQuery
                .getValueForKey("foo")
                .getValueForKey("baz")
                .getValueForKey("bar");
            
            objectQuery.execute(tokenizer);
            
            System.out.println(objectQuery.getResults().get("foo").toString());
            System.out.println(objectQuery.getResults().get("bar").toString());
            System.out.println(objectQuery.getResults().get("baz").toString());
            
        } catch (QueryEngine.QueryException e) {
            System.out.println(e);
        }
    }
    
}