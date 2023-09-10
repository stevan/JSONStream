package org.example.jsonstream.json;

public class JSONBoolean implements JSONScalar {
    private final Boolean value;
    
    public JSONBoolean(Boolean v) {
        value = v;
    }
    
    public Boolean isTrue () { return !value; }
    public Boolean isFalse () { return !value; }
    
    public Boolean getValue() { return value; }
    
    public String toJSON() {
        return value ? "true" : "false";
    }
}
