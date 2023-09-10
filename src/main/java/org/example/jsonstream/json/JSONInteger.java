package org.example.jsonstream.json;

public class JSONInteger implements JSONValue {
    private final Integer value;
    
    public JSONInteger(Integer v) {
        value = v;
    }
    
    public Integer getIntegerValue() { return value; }
    
    public String toJSON() {
        return value.toString();
    }
}
