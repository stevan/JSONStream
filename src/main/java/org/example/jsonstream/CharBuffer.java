package org.example.jsonstream;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CharBuffer {

    String source;
    int index;

    CharBuffer(String src) {
        index = 0;
        source = src;
    }
    
    public Stream<Character> asStream() {
        return Stream.generate(() -> {
            char c = source.charAt(index);
            //System.out.println("stream called with ("+c+") for ["+index+"]");
            index++;
            return c;
        }).limit(source.length() - index);
    }
    
    public Stream<Character> streamWhile(Predicate<Character> predicate) {
        return Stream.iterate(
                source.charAt(index),
                (c) -> predicate.test(source.charAt(index)),
                (c) -> source.charAt(++index)
        ).limit(source.length() - index);
    }

    public Optional<Character> getNext() {
        Optional<Character> c = peek();
        index++;
        return c;
    }

    public Optional<Character> peek() {
        if (isDone()) return Optional.empty();
        Character c = source.charAt(index);
        //System.out.println("peek called with ("+c+")");
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
        return "CharBuffer("+
                        (index != source.length()
                             ? (source.substring(0, index)
                                    + "<<" + source.charAt(index) + ">>"
                                    + source.substring(index + 1))
                             : source)
                        + ")["+index+"]";
    }
}
