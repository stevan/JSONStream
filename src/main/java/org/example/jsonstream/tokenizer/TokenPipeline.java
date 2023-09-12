package org.example.jsonstream.tokenizer;

public class TokenPipeline<C extends TokenConsumer> {
    
    private final C consumer;
    
    public TokenPipeline(C c) { consumer = c; }
    
    public C getConsumer() { return consumer; }
    
    public <T extends Tokens.Token> Tokens.Token pipeToken(T token) {
        consumer.consumeToken(token);
        return token;
    }
}
