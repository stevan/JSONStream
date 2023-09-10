package org.example.jsonstream.json;

public class JSONFloat implements JSONValue {
    private final Float value;
    
    public JSONFloat(Float v) {
        value = v;
    }
    
    public Float getFloatValue() { return value; }
    
    public String toJSON() {
        return value.toString();
    }
}
