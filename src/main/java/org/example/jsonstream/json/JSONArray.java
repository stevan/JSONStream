package org.example.jsonstream.json;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.*;

public class JSONArray implements JSONValue, JSONCollection {
    private final List<JSONValue> items;
    
    public JSONArray() {
        items = List.of();
    }
    public JSONArray(List<JSONValue> i) {
        items = i;
    }
    
    public JSONValue get(Integer i) {
        return items.get(i);
    }
    
    public Integer length() {
        return items.size();
    }
    
    public Stream<JSONValue> stream() {
        return items.stream();
    }
    
    public void forEach(Consumer<JSONValue> f) {
        items.forEach(f);
    }
    
    public Stream<? extends Object> map(Function<JSONValue, ? extends Object> f) {
        return items.stream().map(f);
    }
    
    public Stream<JSONValue> grep(Predicate<JSONValue> f) {
        return items.stream().filter(f);
    }
    
    public List<JSONValue> toList() {
        return List.copyOf(items);
    }
    
    public JSONValue[] toArray() {
        return items.toArray(new JSONValue[]{});
    }
    
    public String toJSON() {
        return "[" +
                   items.stream()
                       .map(JSONValue::toJSON)
                       .collect(Collectors.joining(","))
                   + "]";
    }
}
