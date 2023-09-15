package org.example.jsonstream.tokenizer;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CharBuffer {

    final char[] source;
    int index;
    
    public CharBuffer(String src) {
        index = 0;
        source = src.toCharArray();
    }
    
    public Stream<Character> asStream() {
        return Stream.generate(() -> {
            char c = source[index];
            index++;
            return c;
        }).limit(source.length - index);
    }
    
    public Stream<Character> streamWhile(Predicate<Character> predicate) {
        return Stream.iterate(
                source[index],
                (c) -> predicate.test(source[index]),
                (c) -> source[++index]
        ).limit(source.length - index);
    }

    public Optional<Character> getNext() {
        Optional<Character> c = peek();
        index++;
        return c;
    }

    public Optional<Character> peek() {
        if (isDone()) return Optional.empty();
        Character c = source[index];
        return Optional.of(c);
    }
    
    public boolean isDone() {
        return index >= source.length;
    }

    public void skip(int n) {
        index += n;
    }

    public Optional<Character> skipWhitespaceAndPeek() {
        while (!isDone() && Character.isWhitespace(source[index])) {
            index++;
        }
        return peek();
    }
    
    @Override
    public String toString() {
        String orig = String.valueOf(source);
        return "CharBuffer("+
                        (index < source.length
                             ? (orig.substring(0, index)
                                    + "«" + orig.charAt(index) + "»"
                                    + orig.substring(index + 1))
                             : orig)
                        + ")["+index+"]";
    }
}
