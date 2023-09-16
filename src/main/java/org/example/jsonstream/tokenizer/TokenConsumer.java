package org.example.jsonstream.tokenizer;

public interface TokenConsumer {
    
    // TODO - add a isFull method
    
    void consumeToken(Tokens.NoToken token);
    void consumeToken(Tokens.ErrorToken token);
    void consumeToken(Tokens.StartObject token);
    void consumeToken(Tokens.EndObject token);
    void consumeToken(Tokens.StartProperty token);
    void consumeToken(Tokens.EndProperty token);
    void consumeToken(Tokens.StartArray token);
    void consumeToken(Tokens.EndArray token);
    void consumeToken(Tokens.StartItem token);
    void consumeToken(Tokens.EndItem token);
    void consumeToken(Tokens.AddKey token);
    void consumeToken(Tokens.AddString token);
    void consumeToken(Tokens.AddInt token);
    void consumeToken(Tokens.AddFloat token);
    void consumeToken(Tokens.AddTrue token);
    void consumeToken(Tokens.AddFalse token);
    void consumeToken(Tokens.AddNull token);
    
    default <T extends Tokens.Token> void consumeToken(T token) {
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
}
