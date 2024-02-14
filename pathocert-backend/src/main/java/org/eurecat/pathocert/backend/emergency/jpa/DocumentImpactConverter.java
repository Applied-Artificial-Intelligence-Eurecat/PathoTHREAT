package org.eurecat.pathocert.backend.emergency.jpa;

import org.eurecat.pathocert.backend.close_assessments.data.DocumentImpact;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.LinkedList;

@Converter
public class DocumentImpactConverter implements AttributeConverter<DocumentImpact, String> {
    @Override
    public String convertToDatabaseColumn(DocumentImpact attribute) {
        if (attribute == null) {
            return "";
        }
        return attribute.component1() + "," + attribute.component2() + "," + attribute.component3();
    }

    @Override
    public DocumentImpact convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.equals("")) {
            return new DocumentImpact(0, 0, 0, new LinkedList<>(), new LinkedList<>());
        }
        var sp = dbData.split(",");
        return new DocumentImpact(Integer.parseInt(sp[0]), Integer.parseInt(sp[1]), Integer.parseInt(sp[2]), new LinkedList<>(), new LinkedList<>());
    }
}
