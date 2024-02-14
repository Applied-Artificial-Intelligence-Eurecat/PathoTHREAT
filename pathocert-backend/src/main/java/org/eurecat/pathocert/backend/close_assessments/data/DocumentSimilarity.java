package org.eurecat.pathocert.backend.close_assessments.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.eurecat.pathocert.backend.emergency.jpa.Document;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DocumentSimilarity {
    private Document document;
    private BigDecimal similarity;
    private DocumentImpact impact;
    private DocumentControl control;
}
