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
    
    public Stream<Scans.Scan> stream() {
        return Stream.iterate(
            getNextScan(),
            (t) -> !t.isTerminal(),
            (t) -> getNextScan()
        );
    }
    
    public Iterator<Scans.Scan> iterator() {
        return new Iterator<>() {
            public boolean hasNext() { return hasMore(); }
            public Scans.Scan next() { return getNextScan(); }
        };
    }
    
    public boolean isDone() {
        return index >= source.length;
    }
    public boolean hasMore() {
        return index < source.length;
    }
    
    public Scans.Scan peekNextScan() {
        int temp = index;
        Scans.Scan token = getNextScan();
        index = temp;
        return token;
    }
    
    public Scans.Scan getNextScan() {
        // consume any whitespace
        while (hasMore() && Character.isWhitespace(source[index])) index++;
        // end it if we are done
        if (isDone()) return new Scans.End();
        
        return switch (source[index]) {
            case '{', '}', '[', ']', ',', ':' -> getOperator();
            case 't' -> getKeyword('t', 'r', 'u', 'e');
            case 'f' -> getKeyword('f', 'a', 'l', 's', 'e');
            case 'n' -> getKeyword('n', 'u', 'l', 'l');
            default  -> getConstant();
        };
    }
    
    public void discardNextScan() {
        getNextScan();
    }
    
    // private
    
    private Scans.Scan getOperator() {
        return new Scans.Operator(source[index++] + "");
    }
    
    private Scans.Scan getConstant() {
        return switch (source[index]) {
            case '"' -> getStringConstant();
            case '-' -> getNumericConstant();
            default  ->
                (Character.isDigit(source[index])
                     ? getNumericConstant()
                     : new Scans.Error("Expected number or string, found ("+source[index]+")"));
        };
    }
    
    private Scans.Scan getStringConstant() {
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
        
        return new Scans.Constant(acc.toString(), Scans.TokenType.STRING);
    }
    
    private Scans.Scan getNumericConstant() {
        StringBuilder acc = new StringBuilder();
        
        if (source[index] == '-') {
            acc.append(source[index++]);
        }
        
        Scans.TokenType type = Scans.TokenType.INTEGER;
        
        OUTER: while (hasMore()) {
            switch (source[index]) {
                case '.' -> {
                    acc.append(source[index++]);
                    type = Scans.TokenType.FLOAT;
                }
                case 'e' -> {
                    return new Scans.Error("Scientific notation not supported (yet)");
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
        return new Scans.Constant(acc.toString(), type);
    }
    
    private Scans.Scan getKeyword(char ... expected) {
        StringBuilder acc = new StringBuilder();
        
        for (char c : expected) {
            if (source[index] == c) {
                acc.append(source[index++]);
            } else {
                return new Scans.Error(
                    "Expected keyword(" + Arrays.toString(expected)
                        + ") but got(" + acc + "«" + source[index] + "»)"
                );
            }
        }
        
        return new Scans.Keyword(acc.toString());
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
