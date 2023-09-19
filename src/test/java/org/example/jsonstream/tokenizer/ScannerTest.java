package org.example.jsonstream.tokenizer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.*;

class ScannerTest {
    
    private final String SOURCE = "[\"foo\", 10, { \"bar\" : 3.14 }, true, false, null]";
    
    @Test
    void ScannerTest_Basic () {
        Scanner scanner = new Scanner(SOURCE);
        
        List<ScannerToken> tokens = new ArrayList<>();
        while (scanner.hasMore()) {
            //System.out.println(scanner.peekNextToken());
            tokens.add(scanner.getNextToken());
        }
        
        testTokenList(tokens);
    }
    
    @Test
    void ScannerTest_Stream () {
        Scanner scanner = new Scanner(SOURCE);
        
        List<ScannerToken> tokens = scanner.stream().collect(Collectors.toList());
        
        testTokenList(tokens);
    }
    
    @Test
    void ScannerTest_BasicIterator () {
        Scanner scanner = new Scanner(SOURCE);
        
        Iterator<ScannerToken> tokenIterator = scanner.iterator();
        
        List<ScannerToken> tokens = new ArrayList<>();
        while (tokenIterator.hasNext()) {
            tokens.add(tokenIterator.next());
        }
        
        testTokenList(tokens);
    }
    
    private static void testTokenList(List<ScannerToken> tokens) {
        assertEquals(tokens.size(), 17);
        
        assertTrue(tokens.get(0).isOperator());
        assertEquals(tokens.get(0).getValue(), "[");
        
        assertTrue(tokens.get(1).isConstant());
        assertEquals(tokens.get(1).getValue(), "\"foo\"");
        
        assertTrue(tokens.get(2).isOperator());
        assertEquals(tokens.get(2).getValue(), ",");
        
        assertTrue(tokens.get(3).isConstant());
        assertEquals(tokens.get(3).getValue(), "10");
        
        assertTrue(tokens.get(4).isOperator());
        assertEquals(tokens.get(4).getValue(), ",");
        
        assertTrue(tokens.get(5).isOperator());
        assertEquals(tokens.get(5).getValue(), "{");
        
        assertTrue(tokens.get(6).isConstant());
        assertEquals(tokens.get(6).getValue(), "\"bar\"");
        
        assertTrue(tokens.get(7).isOperator());
        assertEquals(tokens.get(7).getValue(), ":");
        
        assertTrue(tokens.get(8).isConstant());
        assertEquals(tokens.get(8).getValue(), "3.14");
        
        assertTrue(tokens.get(9).isOperator());
        assertEquals(tokens.get(9).getValue(), "}");
        
        assertTrue(tokens.get(10).isOperator());
        assertEquals(tokens.get(10).getValue(), ",");
        
        assertTrue(tokens.get(11).isKeyword());
        assertEquals(tokens.get(11).getValue(), "true");
        
        assertTrue(tokens.get(12).isOperator());
        assertEquals(tokens.get(12).getValue(), ",");
        
        assertTrue(tokens.get(13).isKeyword());
        assertEquals(tokens.get(13).getValue(), "false");
        
        assertTrue(tokens.get(14).isOperator());
        assertEquals(tokens.get(14).getValue(), ",");
        
        assertTrue(tokens.get(15).isKeyword());
        assertEquals(tokens.get(15).getValue(), "null");
        
        assertTrue(tokens.get(16).isOperator());
        assertEquals(tokens.get(16).getValue(), "]");
    }
    
}