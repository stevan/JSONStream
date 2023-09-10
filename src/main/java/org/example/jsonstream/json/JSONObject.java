package org.example.jsonstream.json;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class JSONObject implements JSONValue, JSONCollection {
    private final Map<String, JSONValue> props;
    
    public JSONObject() {
        props = Map.of();
    }
    public JSONObject(Map<String, JSONValue> p) {
        props = p;
    }
    
    // provide some of the Map interface ...
    
    public JSONValue get(String k) {
        return props.get(k);
    }
    
    public Boolean has(String k) {
        return props.containsKey(k);
    }
    
    public List<String> keys() {
        return List.of(props.keySet().toArray(new String[]{}));
    }
    
    public List<JSONValue> values() {
        return List.of(props.values().toArray(new JSONValue[]{}));
    }
    
    public Boolean empty() {
        return props.isEmpty();
    }
    
    public void forEach(BiConsumer<String,JSONValue> f) {
        props.forEach(f);
    }
    
    public Stream<? extends Object> map(BiFunction<String,JSONValue,? extends Object> f) {
        return props.keySet().stream().map((k) -> f.apply(k, props.get(k)));
    }
    
    public Map<String,JSONValue> asMap() {
        return Map.copyOf(props);
    }
    
    public String toJSON() {
        return "{" +
                   props.keySet()
                       .stream()
                       .map((k) -> "\"" + k + "\":" + props.get(k).toJSON())
                       .collect(Collectors.joining(","))
                   + "}";
    }
}
