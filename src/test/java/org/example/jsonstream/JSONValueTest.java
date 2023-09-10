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
            "[1,2]",
            a.grep((i) -> i.getValue() < 3).toJSON()
        );
        assertEquals(
            "[2.5,3.5,4.5,5.5]",
            a.map((i) -> new JSONFloat(i.getValue() + 1.5F)).toJSON()
        );
        assertEquals(
            "[2.5,3.5]",
            a.grep((i) -> i.getValue() < 3)
                 .map((i) -> new JSONFloat(i.getValue() + 1.5F))
                    .toJSON()
        );
    }
    
    @Test
    void JSONArrayTest_BasicFromStream() {
        
        JSONArray<JSONInteger> a = new JSONArray<>(
            List.of(1, 2, 3, 4)
                .stream()
                .map(JSONInteger::new)
                .collect(Collectors.toList())
        );
        
        assertEquals("[1,2,3,4]", a.toJSON());
        
        JSONArray<JSONFloat> a2 = new JSONArray<>(
            a.stream()
                .map((i) -> i.getValue() + 1.5F)
                .map(JSONFloat::new)
                .collect(Collectors.toList())
        );
        
        assertEquals("[2.5,3.5,4.5,5.5]", a2.toJSON());
    }
    
    @Test
    void JSONValueTest_Basic() {
        
        JSONObject<JSONInteger> o = new JSONObject<>(
            Map.of(
                "foo", new JSONInteger(10),
                "bar", new JSONInteger(20),
                "baz", new JSONInteger(30),
                "gorch", new JSONInteger(40),
                "bling", new JSONInteger(50)
            )
        );
        
        assertEquals(o.get("foo").getValue(), 10);
        assertEquals(o.get("bar").getValue(), 20);
        assertEquals(o.get("baz").getValue(), 30);
        assertEquals(o.get("gorch").getValue(), 40);
        assertEquals(o.get("bling").getValue(), 50);
        
        JSONObject<JSONFloat> o2 = o.each((k, v) -> new JSONFloat(v.getValue() + 0.5F));
        
        assertEquals(o2.get("foo").getValue(), 10.5F);
        assertEquals(o2.get("bar").getValue(), 20.5F);
        assertEquals(o2.get("baz").getValue(), 30.5F);
        assertEquals(o2.get("gorch").getValue(), 40.5F);
        assertEquals(o2.get("bling").getValue(), 50.5F);
        
    }
    
}