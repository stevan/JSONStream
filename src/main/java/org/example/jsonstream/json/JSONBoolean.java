package org.example.jsonstream.json;

public class JSONBoolean implements JSONValue {
    private final Boolean value;
    
    public JSONBoolean(Boolean v) {
        value = v;
    }
    
    public Boolean isTrue () { return value == true; }
    public Boolean isFalse () { return value == false; }
    
    public String toJSON() {
        return value ? "true" : "false";
    }
}
