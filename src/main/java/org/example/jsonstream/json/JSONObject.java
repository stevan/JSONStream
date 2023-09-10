package org.example.jsonstream.json;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class JSONObject<T extends JSONValue> implements JSONValue, JSONCollection {
    private final Map<String, T> props;
    
    public JSONObject() { props = Map.of(); }
    public JSONObject(Map<String, T> p) { props = p; }
    
    public T get(String k) {
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
    
    public void forEach(BiConsumer<String,T> f) {
        props.forEach(f);
    }
    
    public <R extends JSONValue> JSONObject<R> each(BiFunction<String,T,R> f) {
        return new JSONObject<R>(
                props.keySet()
                    .stream()
                    .map((k) -> Map.entry(k, f.apply(k, props.get(k))))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
    }
    
    public Map<String,T> asMap() {
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
