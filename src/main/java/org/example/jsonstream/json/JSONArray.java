package org.example.jsonstream.json;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class JSONArray<T extends JSONValue> implements JSONCollection {
    private final List<T> items;
    
    public JSONArray() { items = List.of(); }
    public JSONArray(List<T> i) { items = i; }
    
    public T get(Integer i) {
        return items.get(i);
    }
    
    public Integer length() {
        return items.size();
    }
    
    public void forEach(Consumer<T> f) {
        items.forEach(f);
    }
    
    public Stream<T> stream() {
        return items.stream();
    }
    
    public <R extends JSONValue> JSONArray<R> map(Function<T,R> f) {
        return new JSONArray<R>(items.stream().map(f).collect(Collectors.toList()));
    }
    
    public JSONArray<T> grep(Predicate<T> f) {
        return new JSONArray<T>(items.stream().filter(f).collect(Collectors.toList()));
    }
    
    public List<T> toList() {
        return List.copyOf(items);
    }
    
    public String toJSON() {
        return "[" +
                   items.stream()
                       .map(JSONValue::toJSON)
                       .collect(Collectors.joining(","))
                   + "]";
    }
}
