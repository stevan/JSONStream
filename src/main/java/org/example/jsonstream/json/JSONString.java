package org.example.jsonstream.json;

public class JSONString implements JSONValue {
    private final String value;
    
    public JSONString(String v) { value = v; }
    
    public String getStringValue() { return value; }
    
    public String toJSON() {
        return "\"" + value + "\"";
    }
}
