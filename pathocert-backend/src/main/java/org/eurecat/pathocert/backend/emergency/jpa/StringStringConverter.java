package org.eurecat.pathocert.backend.emergency.jpa;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@Converter
public class StringStringConverter implements AttributeConverter<Set<String>, String> {
    private static final String SPLIT_CHAR = ";";

    @Override
    public String convertToDatabaseColumn(Set<String> stringList) {
        if (stringList == null) {
            return "";
        }
        AtomicReference<String> mystring = new AtomicReference<>("");
        stringList.forEach(e -> {
            mystring.set(mystring.get().concat(e).concat(SPLIT_CHAR));
        });
        String thestring = mystring.toString();
        if (thestring.length() == 0){
            return thestring;
        }
        return thestring.substring(0, thestring.length() - 1);
    }

    @Override
    public Set<String> convertToEntityAttribute(String string) {
        if (string == null){
            return new HashSet<>();
        }
        return new HashSet<>(Arrays.asList(string.split(SPLIT_CHAR)));
    }
}
