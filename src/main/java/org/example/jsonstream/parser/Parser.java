package org.example.jsonstream.parser;

import org.example.jsonstream.tokenizer.*;
import java.util.*;

public class Parser implements TokenConsumer {

    private AST.Node root;

    private final Stack<AST.Node> stack = new Stack<>();
    
    public Boolean hasRoot() {
        return root != null;
    }
    public AST.Node getRoot() {
        return root;
    }
    
    // TODO - these are both error conditions
    //  so do something useful here
    public void consumeToken(Tokens.NoToken token) {}
    public void consumeToken(Tokens.ErrorToken token) {}
    
    // consume the tokens we care about
    
    public void consumeToken(Tokens.StartObject token) {
        AST.ObjectNode o = AST.newObject();
        if ( !stack.empty() ) {
            addValue(o);
        }
        stack.push(o);
    }
    
    public void consumeToken(Tokens.EndObject token) {
        if ( !stack.empty() ) {
            AST.Node top = stack.pop();
            if ( stack.empty() ) {
                root = top;
            }
        }
    }
    
    public void consumeToken(Tokens.StartArray token) {
        AST.ArrayNode a = AST.newArray();
        if ( !stack.empty() ) {
            addValue(a);
        }
        stack.push(a);
    }
    
    public void consumeToken(Tokens.EndArray token) {
        if ( !stack.empty() ) {
            AST.Node top = stack.pop();
            if ( stack.empty() ) {
                root = top;
            }
        }
    }
    
    // skip a few which are not important
    public void consumeToken(Tokens.StartProperty token) {}
    public void consumeToken(Tokens.EndProperty token) {}
    public void consumeToken(Tokens.StartItem token) {}
    public void consumeToken(Tokens.EndItem token) {}
    
    public void consumeToken(Tokens.AddKey token) {
        AST.PropertyNode p = AST.newProperty().addKey(token.getValue());
        stack.push(p);
    }
    
    public void consumeToken(Tokens.AddString token) {
        addValue(AST.newString(token.getValue()));
    }
    
    public void consumeToken(Tokens.AddInt token) {
        addValue(AST.newInt(token.getValue()));
    }
    
    public void consumeToken(Tokens.AddFloat token) {
        addValue(AST.newFloat(token.getValue()));
    }
    
    public void consumeToken(Tokens.AddTrue token) { addValue(AST.newTrue()); }
    public void consumeToken(Tokens.AddFalse token) { addValue(AST.newFalse()); }
    public void consumeToken(Tokens.AddNull token) {
        addValue(AST.newNull());
    }

    private void addValue (AST.Node node) {
        if (stack.peek() instanceof AST.ArrayNode) {
            AST.ArrayNode curr = (AST.ArrayNode) stack.peek();
            curr.addItem( AST.newItem( node ) );
        } else if (stack.peek() instanceof AST.PropertyNode) {
            AST.PropertyNode prop = (AST.PropertyNode) stack.pop();
            if (stack.peek() instanceof AST.ObjectNode) {
                AST.ObjectNode curr = (AST.ObjectNode) stack.peek();
                prop.addValue(node);
                curr.addProperty(prop);
            } else {
                // TODO - throw an error here
            }
        } else {
            // TODO - throw an error here
        }
    }
}
