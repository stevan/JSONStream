package org.example.jsonstream.json;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class JSONObject implements JSONValue, JSONCollection {
    private final Map<String, JSONValue> props;
    
    public JSONObject() { props = Map.of(); }
    public JSONObject(Map<String, JSONValue> p) { props = p; }
    
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
    
    public JSONObject each(BiFunction<String,JSONValue,JSONValue> f) {
        return new JSONObject(
                props.keySet()
                    .stream()
                    .map((k) -> Map.entry(k, f.apply(k, props.get(k))))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
    }
    
    public Map<String,JSONValue> asMap() {
        return Map.copyOf(props);
    }
    
    public static JSONObject copyOf(JSONObject o) {
        return new JSONObject(o.asMap());
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
