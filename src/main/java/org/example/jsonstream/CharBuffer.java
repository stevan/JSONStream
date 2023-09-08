package org.example.jsonstream;

import java.util.Optional;

public class CharBuffer {

    String source;
    int index;

    CharBuffer(String src) {
        index = 0;
        source = src;
    }

    public Optional<Character> getNext() {
        Optional<Character> c = peek();
        index++;
        return c;
    }

    public Optional<Character> peek() {
        if (isDone()) return Optional.empty();
        Character c = source.charAt(index);
        return Optional.of(c);
    }

    public boolean isDone() {
        return index >= source.length();
    }

    public void skip(int n) {
        index += n;
    }

    public Optional<Character> skipWhitespaceAndPeek() {
        while (!isDone() && Character.isWhitespace(source.charAt(index))) {
            index++;
        }
        return peek();
    }
    
    @Override
    public String toString() {
        return "CharBuffer{"+ source.substring(0, index)
                        + "<<" + source.charAt(index) + ">>"
                        + source.substring(index + 1)
                        + "}["+index+"]";
    }
}
