package org.example.jsonstream;

import java.util.Stack;

public class Parser {

    AST.Node root;
    final Stack<AST.Node> stack = new Stack<>();
    
    public boolean hasRoot() { return root != null; }
    public AST.Node getRoot() {
        return root;
    }
    
    public void consumeToken(Tokens.Token token) {
        switch (token.getType()) {
            case START_ARRAY:
                AST.ArrayNode a = AST.newArray();
                if ( !stack.empty() ) {
                    addValue(a );
                }
                stack.push(a);
                break;
            case START_OBJECT:
                AST.ObjectNode o = AST.newObject();
                if ( !stack.empty() ) {
                    addValue(o );
                }
                stack.push(o);
                break;

            case END_ARRAY:
            case END_OBJECT:
                if ( !stack.empty() ) {
                    AST.Node top = stack.pop();
                    if ( stack.empty() ) {
                        root = top;
                    }
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
                addValue(AST.newTrue() );
                break;
            case ADD_FALSE:
                addValue(AST.newFalse() );
                break;
            case ADD_NULL:
                addValue(AST.newNull() );
                break;
            case ADD_STRING:
                addValue(AST.newString( ((Tokens.AddString) token).getValue() ) );
                break;
            case ADD_INT:
                addValue(AST.newInt( ((Tokens.AddInt) token).getValue() ) );
                break;
            case ADD_FLOAT:
                addValue(AST.newFloat( ((Tokens.AddFloat) token).getValue() ) );
                break;
        }
    }

    private void addValue (AST.Node node) {
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
