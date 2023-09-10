package org.example.jsonstream.json;

public class JSONFloat implements JSONScalar {
    private final Float value;
    
    public JSONFloat(Float v) { value = v; }
    
    public Float getValue() { return value; }
    
    public String toJSON() {
        return value.toString();
    }
}
