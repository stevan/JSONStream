package org.example.jsonstream.tokenizer;

public class TokenConsumer {
    
    public <T extends Tokens.Token> void consumeToken(T token) {
        switch (token.getType()) {
            case NO_TOKEN:
                consumeToken((Tokens.NoToken) token);
                break;
            case ERROR_TOKEN:
                consumeToken((Tokens.ErrorToken) token);
                break;
            case START_ARRAY:
                consumeToken((Tokens.StartArray) token);
                break;
            case END_ARRAY:
                consumeToken((Tokens.EndArray) token);
                break;
            case START_ITEM:
                consumeToken((Tokens.StartItem) token);
                break;
            case END_ITEM:
                consumeToken((Tokens.EndItem) token);
                break;
            case START_OBJECT:
                consumeToken((Tokens.StartObject) token);
                break;
            case END_OBJECT:
                consumeToken((Tokens.EndObject) token);
                break;
            case START_PROPERTY:
                consumeToken((Tokens.StartProperty) token);
                break;
            case END_PROPERTY:
                consumeToken((Tokens.EndProperty) token);
                break;
            case ADD_KEY:
                consumeToken((Tokens.AddKey) token);
                break;
            case ADD_TRUE:
                consumeToken((Tokens.AddTrue) token);
                break;
            case ADD_FALSE:
                consumeToken((Tokens.AddFalse) token);
                break;
            case ADD_NULL:
                consumeToken((Tokens.AddNull) token);
                break;
            case ADD_STRING:
                consumeToken((Tokens.AddString) token);
                break;
            case ADD_INT:
                consumeToken((Tokens.AddInt) token);
                break;
            case ADD_FLOAT:
                consumeToken((Tokens.AddFloat) token);
                break;
        }
    }
    
    public void consumeToken(Tokens.NoToken token) {}
    public void consumeToken(Tokens.ErrorToken token) {}
    public void consumeToken(Tokens.StartObject token) {}
    public void consumeToken(Tokens.EndObject token) {}
    public void consumeToken(Tokens.StartProperty token) {}
    public void consumeToken(Tokens.EndProperty token) {}
    public void consumeToken(Tokens.StartArray token) {}
    public void consumeToken(Tokens.EndArray token) {}
    public void consumeToken(Tokens.StartItem token) {}
    public void consumeToken(Tokens.EndItem token) {}
    public void consumeToken(Tokens.AddKey token) {}
    public void consumeToken(Tokens.AddString token) {}
    public void consumeToken(Tokens.AddInt token) {}
    public void consumeToken(Tokens.AddFloat token) {}
    public void consumeToken(Tokens.AddTrue token) {}
    public void consumeToken(Tokens.AddFalse token) {}
    public void consumeToken(Tokens.AddNull token) {}
}
