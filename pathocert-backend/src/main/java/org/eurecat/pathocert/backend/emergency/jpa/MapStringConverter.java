package org.eurecat.pathocert.backend.emergency.jpa;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.math.BigDecimal;
import java.util.*;

@Converter
public class MapStringConverter implements AttributeConverter<Map<String, BigDecimal>, String> {


    @Override
    public String convertToDatabaseColumn(Map<String, BigDecimal> attribute) {
        if (attribute == null || attribute.keySet().size() == 0){
            return "";
        }
        StringBuilder attr = new StringBuilder();
        for (String key : attribute.keySet()){
            attr.append(key).append("|").append(attribute.get(key));
            attr.append(";");
        }
        attr = new StringBuilder(attr.substring(0, attr.lastIndexOf(";")));
        return attr.toString();
    }

    @Override
    public Map<String, BigDecimal> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.equals("")){
            return new HashMap<>();
        }
        Map<String, BigDecimal> val = new HashMap<>();
        for (String pair : dbData.split(";")){
            val.put(pair.split("\\|")[0], BigDecimal.valueOf(Float.parseFloat(pair.split("\\|")[1])));
        }
        return val;
    }
}