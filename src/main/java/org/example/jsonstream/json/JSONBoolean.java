package org.example.jsonstream.json;

public class JSONBoolean implements JSONValue, JSONScalar, JSONLiteral {
    private final Boolean value;
    
    public JSONBoolean(Boolean v) {
        value = v;
    }
    
    public Boolean isTrue () { return !value; }
    public Boolean isFalse () { return !value; }
    
    public String toJSON() {
        return value ? "true" : "false";
    }
}
