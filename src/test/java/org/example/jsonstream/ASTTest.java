package org.example.jsonstream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ASTTest {
    
    @Test
    void ASTTest_AllTheThings () {
        
        AST.ObjectNode o = AST.newObject()
            .addProperty(
                AST.newProperty()
                    .addKey("foo")
                    .addValue(AST.newTrue()))
            .addProperty(
                AST.newProperty()
                    .addKey("bar")
                    .addValue(
                        AST.newArray()
                            .addItem(AST.newItem(AST.newNull()))
                            .addItem(AST.newItem(AST.newInt(10)))
                            .addItem(AST.newItem(AST.newObject()))))
            .addProperty(
                AST.newProperty()
                    .addKey("baz")
                    .addValue(
                        AST.newArray()
                            .addItem(AST.newItem(AST.newFalse()))
                            .addItem(AST.newItem(AST.newFloat(3.14F)))
                            .addItem(AST.newItem(AST.newArray()))
                            .addItem(AST.newItem(AST.newString("goorch")))));
        
        assertEquals("{\"foo\":true,\"bar\":[null,10,{}],\"baz\":[false,3.14,[],\"goorch\"]}", o.toJSON());
    }
    
}