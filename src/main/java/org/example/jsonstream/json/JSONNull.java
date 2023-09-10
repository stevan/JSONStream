package org.example.jsonstream.json;

public class JSONNull implements JSONValue, JSONScalar, JSONLiteral {
    
    public JSONNull() {}
    
    public String toJSON() {
        return "null";
    }
}
