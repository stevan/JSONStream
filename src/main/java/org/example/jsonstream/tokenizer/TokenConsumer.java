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
    
    public void consumeToken(Tokens.NoToken token) { System.out.println("NoToken"); }
    public void consumeToken(Tokens.ErrorToken token) { System.out.println("ErrorToken"); }
    public void consumeToken(Tokens.StartObject token) { System.out.println("StartObject"); }
    public void consumeToken(Tokens.EndObject token) { System.out.println("EndObject"); }
    public void consumeToken(Tokens.StartProperty token) { System.out.println("StartProperty"); }
    public void consumeToken(Tokens.EndProperty token) { System.out.println("EndProperty"); }
    public void consumeToken(Tokens.StartArray token) { System.out.println("StartArray"); }
    public void consumeToken(Tokens.EndArray token) { System.out.println("EndArray"); }
    public void consumeToken(Tokens.StartItem token) { System.out.println("StartItem"); }
    public void consumeToken(Tokens.EndItem token) { System.out.println("EndItem"); }
    public void consumeToken(Tokens.AddKey token) { System.out.println("AddKey"); }
    public void consumeToken(Tokens.AddString token) { System.out.println("AddString"); }
    public void consumeToken(Tokens.AddInt token) { System.out.println("AddInt"); }
    public void consumeToken(Tokens.AddFloat token) { System.out.println("AddFloat"); }
    public void consumeToken(Tokens.AddTrue token) { System.out.println("AddTrue"); }
    public void consumeToken(Tokens.AddFalse token) { System.out.println("AddFalse"); }
    public void consumeToken(Tokens.AddNull token) { System.out.println("AddNull"); }
}
