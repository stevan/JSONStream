package org.example.jsonstream.tokenizer;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;

public class Scanner {
    
    private final char[] source;
    private int index;
    
    public Scanner(String source) {
        this.source = source.toCharArray();
        this.index = 0;
    }
    
    // public
    
    public Stream<Scan> stream() {
        return Stream.iterate(
            getNextToken(),
            (t) -> !t.isTerminal(),
            (t) -> getNextToken()
        );
    }
    
    public Iterator<Scan> iterator() {
        return new Iterator<>() {
            public boolean hasNext() { return hasMore(); }
            public Scan next() { return getNextToken(); }
        };
    }
    
    public boolean isDone() {
        return index >= source.length;
    }
    
    public boolean hasMore() {
        return index < source.length;
    }
    
    public Scan peekNextToken() {
        int temp = index;
        Scan token = getNextToken();
        index = temp;
        return token;
    }
    
    public void discardNextToken() {
        getNextToken();
    }
    
    public Scan getNextToken() {
        // consume any whitespace
        while (hasMore() && Character.isWhitespace(source[index])) index++;
        // end it if we are done
        if (isDone()) return Scan.end();
        
        return switch (source[index]) {
            case '{', '}', '[', ']', ',', ':' -> getOperator();
            case 't' -> getKeyword('t', 'r', 'u', 'e');
            case 'f' -> getKeyword('f', 'a', 'l', 's', 'e');
            case 'n' -> getKeyword('n', 'u', 'l', 'l');
            default  -> getConstant();
        };
    }
    
    // private
    
    private Scan getOperator() {
        return Scan.operator(source[index++] + "");
    }
    
    private Scan getConstant() {
        return switch (source[index]) {
            case '"' -> getStringConstant();
            case '-' -> getNumericConstant();
            default  ->
                (Character.isDigit(source[index])
                     ? getNumericConstant()
                     : Scan.error("Expected number or string, found ("+source[index]+")"));
        };
    }
    
    private Scan getStringConstant() {
        StringBuilder acc = new StringBuilder();
        // grab the quote
        acc.append(source[index++]);
        
        while (hasMore()) {
            acc.append(source[index++]);
            
            if (source[index] == '"') {
                acc.append(source[index++]);
                break;
            }
        }
        
        // TODO - check for unterminated string
        //  constant and escape sequences
        
        return Scan.constant(acc.toString(), Scan.TokenType.STRING);
    }
    
    private Scan getNumericConstant() {
        StringBuilder acc = new StringBuilder();
        
        if (source[index] == '-') {
            acc.append(source[index++]);
        }
        
        Scan.TokenType type = Scan.TokenType.INTEGER;
        
        OUTER: while (hasMore()) {
            switch (source[index]) {
                case '.' -> {
                    acc.append(source[index++]);
                    type = Scan.TokenType.FLOAT;
                }
                case 'e' -> {
                    return Scan.error("Scientific notation not supported (yet)");
                }
                default -> {
                    if (Character.isDigit(source[index])) {
                        acc.append(source[index++]);
                    } else {
                        break OUTER;
                    }
                }
            }
        }
        return Scan.constant(acc.toString(), type);
    }
    
    private Scan getKeyword(char ... expected) {
        StringBuilder acc = new StringBuilder();
        
        for (char c : expected) {
            if (source[index] == c) {
                acc.append(source[index++]);
            } else {
                return Scan.error(
                    "Expected keyword(" + Arrays.toString(expected)
                        + ") but got(" + acc + "«" + source[index] + "»)"
                );
            }
        }
        
        return Scan.keyword(acc.toString());
    }
    
    @Override
    public String toString() {
        String orig = String.valueOf(source);
        return "Scanner `"+
                   (index < source.length
                        ? (orig.substring(0, index)
                               + "«" + orig.charAt(index) + "»"
                               + orig.substring(index + 1))
                        : orig + "«»")
                   + "` (length: "+source.length + ", index: "+index+")";
    }
}
