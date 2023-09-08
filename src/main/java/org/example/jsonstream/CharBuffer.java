package org.example.jsonstream;

import java.util.Optional;

public class CharBuffer {

    final char[] chars;
    int index;

    CharBuffer(String source) {
        index = 0;
        chars = source.toCharArray();
    }

    public Optional<Character> get() {
        Optional<Character> c = peek();
        index++;
        return c;
    }

    public Optional<Character> peek() {
        if (isDone()) return Optional.empty();
        Character c = chars[index];
        return Optional.of(c);
    }

    public boolean isDone() {
        return index >= chars.length;
    }

    public void skip(int n) {
        index += n;
    }

    public Optional<Character> skipWhitespaceAndPeek() {
        while (!isDone() && Character.isWhitespace(chars[index])) {
            index++;
        }
        return peek();
    }
}
