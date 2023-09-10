package org.example.jsonstream.json;

public class JSONInteger implements JSONScalar {
    private final Integer value;
    
    public JSONInteger(Integer v) { value = v; }
    
    public Integer getValue() { return value; }
    
    public String toJSON() {
        return value.toString();
    }
}
