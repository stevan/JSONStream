package org.example.jsonstream.tokenizer;

public abstract class TokenConsumer {
    
    public abstract void consumeToken(Tokens.NoToken token);
    public abstract void consumeToken(Tokens.ErrorToken token);
    public abstract void consumeToken(Tokens.StartObject token);
    public abstract void consumeToken(Tokens.EndObject token);
    public abstract void consumeToken(Tokens.StartProperty token);
    public abstract void consumeToken(Tokens.EndProperty token);
    public abstract void consumeToken(Tokens.StartArray token);
    public abstract void consumeToken(Tokens.EndArray token);
    public abstract void consumeToken(Tokens.StartItem token);
    public abstract void consumeToken(Tokens.EndItem token);
    public abstract void consumeToken(Tokens.AddKey token);
    public abstract void consumeToken(Tokens.AddString token);
    public abstract void consumeToken(Tokens.AddInt token);
    public abstract void consumeToken(Tokens.AddFloat token);
    public abstract void consumeToken(Tokens.AddTrue token);
    public abstract void consumeToken(Tokens.AddFalse token);
    public abstract void consumeToken(Tokens.AddNull token);
    
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
}
