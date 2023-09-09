package org.example.jsonstream;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class AST {
    
    public interface Node {
        String toJSON ();
    }
    
    public static class ObjectNode implements Node {
        ArrayList<PropertyNode> properties = new ArrayList<>();
        
        public ObjectNode addProperty(PropertyNode prop) {
            properties.add(prop);
            return this;
        }
        
        public String toJSON() {
            return "{"
                    + properties.stream()
                          .map((n) -> n.toJSON())
                          .collect(Collectors.joining(","))
                    + "}";
        }
    }
    
    public static class ArrayNode implements Node {
        ArrayList<ItemNode> items = new ArrayList<>();
        
        public ArrayNode addItem(ItemNode item) {
            items.add(item);
            return this;
        }
        
        public String toJSON() {
            return "["
                    + items.stream()
                          .map((n) -> n.toJSON())
                          .collect(Collectors.joining(","))
                    + "]";
        }
    }
    
    public static class PropertyNode implements Node {
        String key;
        Node value;
        
        PropertyNode() {}
        PropertyNode(String k) { key = k; }
        PropertyNode(String k, Node v) { key = k; value = v; }
        
        public PropertyNode addKey(String k) { key = k;   return this; }
        public PropertyNode addValue(Node v) { value = v; return this; }
        
        public String toJSON() {
            return "\"" + key + "\":" + value.toJSON();
        }
    }
    
    public static class ItemNode implements Node {
        Node item;
        
        ItemNode() {}
        ItemNode(Node i) { item = i; }
        
        public ItemNode addValue(Node i) { item = i; return this; }
        
        public String toJSON() {
            return item.toJSON();
        }
    }
    
    public static class StringNode implements Node {
        String value;
        
        StringNode(String v) { value = v; }
        
        public String toJSON() {
            return "\"" + value + "\"";
        }
    }
    
    public static class IntNode implements Node {
        Integer value;
        
        IntNode(Integer v) { value = v; }
        
        public String toJSON() {
            return value.toString();
        }
    }
    
    public static class FloatNode implements Node {
        Float value;
        
        FloatNode(Float v) { value = v; }
        
        public String toJSON() {
            return value.toString();
        }
    }
    
    public static class TrueNode implements Node {
        public String toJSON() {
            return "true";
        }
    }
    
    public static class FalseNode implements Node {
        public String toJSON() {
            return "false";
        }
    }
    
    public static class NullNode implements Node {
        public String toJSON() {
            return "null";
        }
    }
    
    public static ObjectNode newObject() { return new ObjectNode(); }
    public static ArrayNode newArray() { return new ArrayNode(); }
    public static PropertyNode newProperty() { return new PropertyNode(); }
    public static ItemNode newItem(Node n) { return new ItemNode(n); }
    public static StringNode newString(String s) { return new StringNode(s); }
    public static IntNode newInt(Integer i) { return new IntNode(i); }
    public static FloatNode newFloat(Float f) { return new FloatNode(f); }
    public static TrueNode newTrue() { return new TrueNode(); }
    public static FalseNode newFalse() { return new FalseNode(); }
    public static NullNode newNull() { return new NullNode(); }
}
