package org.example.jsonstream;

import java.util.*;
import java.util.stream.Collectors;

import org.example.jsonstream.json.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JSONValueTest {
    
    @Test
    void JSONArrayTest_Basic() {
        
        JSONArray<JSONInteger> a = new JSONArray<>(
            List.of(
                new JSONInteger(1),
                new JSONInteger(2),
                new JSONInteger(3),
                new JSONInteger(4)
            )
        );
        
        assertEquals("[1,2,3,4]", a.toJSON());
        assertEquals(
            "[2.5,3.5,4.5,5.5]",
            a.map((i) -> new JSONFloat(i.getValue() + 1.5F)).toJSON()
        );
    }
    
    @Test
    void JSONValueTest_Basic() {
        
        JSONObject o = new JSONObject(
            Map.of(
                "foo", new JSONInteger(10),
                "bar", new JSONBoolean(true),
                "baz", new JSONNull(),
                "gorch", new JSONString("Hello World!"),
                "bling", new JSONArray<>(
                    List.of(
                        new JSONBoolean(false),
                        new JSONArray<>(),
                        new JSONFloat(3.14F),
                        new JSONObject()
                    )
                )
            )
        );
        
        
        
    }
    
}