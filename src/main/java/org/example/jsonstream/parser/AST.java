package org.example.jsonstream.parser;

import java.util.*;
import java.util.stream.*;

public class AST {
    
    public interface Node {
        String toJSON ();
    }
    
    public static class ObjectNode implements Node {
        private final ArrayList<PropertyNode> properties = new ArrayList<>();
        
        public List<PropertyNode> properties() { return properties; }
        
        public ObjectNode addProperty(PropertyNode prop) {
            properties.add(prop);
            return this;
        }
        
        public String toJSON() {
            return "{"
                    + properties.stream()
                          .map(PropertyNode::toJSON)
                          .collect(Collectors.joining(","))
                    + "}";
        }
    }
    
    public static class ArrayNode implements Node {
        private final ArrayList<ItemNode> items = new ArrayList<>();
        
        public List<ItemNode> items() { return items; }
        
        public ArrayNode addItem(ItemNode item) {
            items.add(item);
            return this;
        }
        
        public String toJSON() {
            return "["
                    + items.stream()
                          .map(ItemNode::toJSON)
                          .collect(Collectors.joining(","))
                    + "]";
        }
    }
    
    public static class PropertyNode implements Node {
        private String key;
        private Node value;
        
        public PropertyNode() {}
        
        public String getKey() { return key; }
        public Node getValue() { return value; }
        
        public PropertyNode addKey(String k) { key = k;   return this; }
        public PropertyNode addValue(Node v) { value = v; return this; }
        
        public String toJSON() {
            return "\"" + key + "\":" + value.toJSON();
        }
    }
    
    public static class ItemNode implements Node {
        private Node item;
        
        public ItemNode(Node i) { item = i; }
        
        public Node getValue() { return item; }
        public ItemNode addValue(Node i) { item = i; return this; }
        
        public String toJSON() {
            return item.toJSON();
        }
    }
    
    public static class StringNode implements Node {
        private final String value;
        
        public StringNode(String v) { value = v; }
        
        public String getValue() { return value; }
        
        public String toJSON() {
            return "\"" + value + "\"";
        }
    }
    
    public static class IntNode implements Node {
        private final Integer value;
        
        public IntNode(Integer v) { value = v; }
        
        public Integer getValue() { return value; }
        
        public String toJSON() {
            return value.toString();
        }
    }
    
    public static class FloatNode implements Node {
        private final Float value;
        
        public FloatNode(Float v) { value = v; }
        
        public Float getValue() { return value; }
        
        public String toJSON() {
            return value.toString();
        }
    }
    
    public static class TrueNode implements Node {
        
        public boolean getValue() { return true; }
        
        public String toJSON() {
            return "true";
        }
    }
    
    public static class FalseNode implements Node {
        
        public boolean getValue() { return false; }
        
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
