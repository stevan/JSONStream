package org.example.jsonstream;

import java.util.*;
import java.util.stream.Collectors;

import org.example.jsonstream.json.*;
import org.junit.jupiter.api.Test;

class JSONValueTest {
    
    @Test
    void JSONValueTest_Basic() {
        
        JSONObject o = new JSONObject(
            Map.of(
                "foo", new JSONInteger(10),
                "bar", new JSONBoolean(true),
                "baz", new JSONNull(),
                "gorch", new JSONString("Hello World!"),
                "bling", new JSONArray(
                    List.of(
                        new JSONBoolean(false),
                        new JSONArray(),
                        new JSONFloat(3.14F),
                        new JSONObject()
                    )
                )
            )
        );
        
        System.out.println(o.keys().toString());
        
        System.out.println(o.values().stream().map(JSONValue::toJSON).collect(Collectors.joining(", ")));
        
        o.forEach((k, v) -> System.out.println(k + " => " + v.toJSON()));
        o.map((k, v) -> "<" + k + " => " + v.toJSON() + ">").forEach(System.out::println);
        
        JSONArray bling = (JSONArray) o.get("bling");
        bling.forEach((v) -> System.out.println(v.toJSON()));
        bling.map(JSONValue::toJSON).forEach(System.out::println);
        bling.grep((v) -> v instanceof JSONCollection).map(JSONValue::toJSON).forEach(System.out::println);
        
        
        System.out.println(o.toJSON());
        
    }
    
}