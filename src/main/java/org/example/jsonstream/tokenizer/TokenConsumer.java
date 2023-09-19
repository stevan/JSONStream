package org.example.jsonstream.tokenizer;

public interface TokenConsumer {
    
    // TODO - add a isFull method
    
    void consumeToken(Tokens.NoToken       token);
    void consumeToken(Tokens.ErrorToken    token);
    void consumeToken(Tokens.StartObject   token);
    void consumeToken(Tokens.EndObject     token);
    void consumeToken(Tokens.StartProperty token);
    void consumeToken(Tokens.EndProperty   token);
    void consumeToken(Tokens.StartArray    token);
    void consumeToken(Tokens.EndArray      token);
    void consumeToken(Tokens.StartItem     token);
    void consumeToken(Tokens.EndItem       token);
    void consumeToken(Tokens.AddKey        token);
    void consumeToken(Tokens.AddString     token);
    void consumeToken(Tokens.AddInt        token);
    void consumeToken(Tokens.AddFloat      token);
    void consumeToken(Tokens.AddTrue       token);
    void consumeToken(Tokens.AddFalse      token);
    void consumeToken(Tokens.AddNull       token);
    
    default <T extends Tokens.Token> void consumeToken(T token) {
        switch (token.getType()) {
            case NO_TOKEN       -> consumeToken((Tokens.NoToken)      token);
            case ERROR_TOKEN    -> consumeToken((Tokens.ErrorToken)    token);
            case START_ARRAY    -> consumeToken((Tokens.StartArray)    token);
            case END_ARRAY      -> consumeToken((Tokens.EndArray)      token);
            case START_ITEM     -> consumeToken((Tokens.StartItem)     token);
            case END_ITEM       -> consumeToken((Tokens.EndItem)       token);
            case START_OBJECT   -> consumeToken((Tokens.StartObject)   token);
            case END_OBJECT     -> consumeToken((Tokens.EndObject)     token);
            case START_PROPERTY -> consumeToken((Tokens.StartProperty) token);
            case END_PROPERTY   -> consumeToken((Tokens.EndProperty)   token);
            case ADD_KEY        -> consumeToken((Tokens.AddKey)        token);
            case ADD_TRUE       -> consumeToken((Tokens.AddTrue)       token);
            case ADD_FALSE      -> consumeToken((Tokens.AddFalse)      token);
            case ADD_NULL       -> consumeToken((Tokens.AddNull)       token);
            case ADD_STRING     -> consumeToken((Tokens.AddString)     token);
            case ADD_INT        -> consumeToken((Tokens.AddInt)        token);
            case ADD_FLOAT      -> consumeToken((Tokens.AddFloat)      token);
        }
    }
}
