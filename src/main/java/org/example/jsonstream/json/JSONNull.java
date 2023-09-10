package org.example.jsonstream.json;

public class JSONNull implements JSONScalar {
    
    public JSONNull() {}
    
    public String toJSON() {
        return "null";
    }
}
