package org.eurecat.pathocert.backend.close_assessments;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DocSim {
    public BigDecimal similarity;
    public String document_title;
}
