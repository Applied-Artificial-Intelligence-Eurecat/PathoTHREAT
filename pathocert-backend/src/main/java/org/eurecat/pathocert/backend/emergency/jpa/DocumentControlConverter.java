package org.eurecat.pathocert.backend.emergency.jpa;

import org.eurecat.pathocert.backend.close_assessments.data.DocumentControl;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Converter
public class DocumentControlConverter implements AttributeConverter<DocumentControl, String> {
    @Override
    public String convertToDatabaseColumn(DocumentControl attribute) {
        if (attribute == null) {
            return "";
        }
        return String.join(",", attribute.component1()) +
                ";" +
                String.join(",", attribute.component2()) +
                ";" +
                String.join(",", attribute.component3());
    }

    @Override
    public DocumentControl convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.equals("")) {
            return new DocumentControl(new LinkedList<>(), new LinkedList<>(), new LinkedList<>());
        }
        List<String> monitoring;
        List<String> restoration;
        List<String> prevention;
        int first_colon = dbData.indexOf(";");
        if (first_colon == 0){
            monitoring = new LinkedList<>();
        } else {
            monitoring = new LinkedList<>(Arrays.asList(dbData.substring(0, first_colon).split(",")));
        }
        int last_colon = dbData.lastIndexOf(";");
        if (last_colon == first_colon + 1){
            restoration = new LinkedList<>();
        } else {
            restoration = new LinkedList<>(Arrays.asList(dbData.substring(first_colon + 1, last_colon).split(",")));
        }
        int length = dbData.length();
        if (last_colon == length - 1){
            prevention = new LinkedList<>();
        } else {
            prevention = new LinkedList<>(Arrays.asList(dbData.substring(last_colon + 1).split(",")));
        }
        return new DocumentControl(monitoring, restoration, prevention);
    }
}
