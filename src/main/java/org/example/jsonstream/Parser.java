package org.example.jsonstream;

import java.util.Optional;
import java.util.Stack;

public class Parser {

    AST.Node root;
    Stack<AST.Node> stack = new Stack<>();
    
    public boolean hasRoot() { return root != null; }
    public AST.Node getRoot() {
        return root;
    }
    
    public void consumeToken(Tokens.Token token) {
        switch (token.getType()) {
            case START_ARRAY:
                AST.ArrayNode a = AST.newArray();
                if ( root == null ) {
                    root = a;
                } else {
                    addValue( token, a );
                }
                stack.push(a);
                break;
            case START_OBJECT:
                AST.ObjectNode o = AST.newObject();
                if ( root == null ) {
                    root = o;
                } else {
                    addValue( token, o );
                }
                stack.push(o);
                break;

            case END_ARRAY:
            case END_OBJECT:
                if ( !stack.empty() ) {
                    stack.pop();
                }
                break;

            case START_ITEM:
            case END_ITEM:
            case START_PROPERTY:
            case END_PROPERTY:
                break;

            case ADD_KEY:
                AST.PropertyNode p = AST.newProperty().addKey(((Tokens.AddKey) token).getValue());
                stack.push(p);
                break;

            case ADD_TRUE:
                addValue( token, AST.newTrue() );
                break;
            case ADD_FALSE:
                addValue( token, AST.newFalse() );
                break;
            case ADD_NULL:
                addValue( token, AST.newNull() );
                break;
            case ADD_STRING:
                addValue( token, AST.newString( ((Tokens.AddString) token).getValue() ) );
                break;
            case ADD_INT:
                addValue( token, AST.newInt( ((Tokens.AddInt) token).getValue() ) );
                break;
            case ADD_FLOAT:
                addValue( token, AST.newFloat( ((Tokens.AddFloat) token).getValue() ) );
                break;
        }
    }

    private void addValue (Tokens.Token token, AST.Node node) {
        if ( stack.peek() instanceof AST.ArrayNode ) {
            AST.ArrayNode curr = (AST.ArrayNode) stack.peek();
            curr.addItem( AST.newItem( node ) );
        }
        else {
            AST.PropertyNode prop = (AST.PropertyNode) stack.pop();
            AST.ObjectNode curr = (AST.ObjectNode) stack.peek();
            prop.addValue( node );
            curr.addProperty( prop );
        }
    }
}
