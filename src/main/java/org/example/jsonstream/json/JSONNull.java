package org.example.jsonstream.json;

public class JSONNull implements JSONValue {
    
    public JSONNull() {}
    
    public String toJSON() {
        return "null";
    }
}
