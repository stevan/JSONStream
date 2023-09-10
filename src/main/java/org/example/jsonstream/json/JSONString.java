package org.example.jsonstream.json;

public class JSONString implements JSONScalar {
    private final String value;
    
    public JSONString(String v) { value = v; }
    
    public String getValue() { return value; }
    
    public String toJSON() {
        return "\"" + value + "\"";
    }
}
