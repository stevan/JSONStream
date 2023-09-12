package org.example.jsonstream.json;

import org.example.jsonstream.json.annotations.*;
import org.example.jsonstream.tokenizer.*;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.*;

class DecoderTest {
    
    public static class Person {
        String name;
        Integer age;
        
        @JSONConstructor
        public Person(
            @JSONProperty("name") String n,
            @JSONProperty("age") Integer a) {
            name = n;
            age = a;
        }
        
        @Override
        public String toString() {
            return "Person{" + name + "}[" + age + "]";
        }
    }
    
    @Test
    void DecoderTest () throws InstantiationException {
        CharBuffer buffer = new CharBuffer("{\"name\":\"Bob\"}");
        Tokenizer tokenizer = new Tokenizer(buffer);
        
        Map<String,Object> json = Map.of("name", "Bob", "age", 10);
        
        Class<Person> klass = Person.class;
        
        List<Person> people = Arrays.stream(klass.getDeclaredConstructors())
              .filter(c -> c.isAnnotationPresent(JSONConstructor.class))
              .map(c -> {
                  Object[] args = Arrays.stream(c.getParameters())
                      .map(param -> param.getAnnotation(JSONProperty.class).value())
                      .map(field -> json.get(field))
                      .collect(Collectors.toList())
                      .toArray(new Object[]{});
                  
                  Person p = null;
                  try {
                      p = (Person) c.newInstance(args);
                  } catch (Exception e) {
                      System.out.println("Uh OH!" + e);
                  }
                  
                  return Optional.ofNullable(p);
              })
              .filter(Optional::isPresent)
              .map(Optional::get)
              .collect(Collectors.toList());
        
        System.out.println(people);
    }
    
}