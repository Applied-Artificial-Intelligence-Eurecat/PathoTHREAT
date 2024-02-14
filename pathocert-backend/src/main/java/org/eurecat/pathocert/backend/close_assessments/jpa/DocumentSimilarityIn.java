package org.eurecat.pathocert.backend.close_assessments.jpa;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.eurecat.pathocert.backend.close_assessments.data.DocumentControl;
import org.eurecat.pathocert.backend.close_assessments.data.DocumentImpact;
import org.eurecat.pathocert.backend.emergency.jpa.Document;
import org.hibernate.annotations.Type;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@AllArgsConstructor
@Embeddable
@NoArgsConstructor
public class DocumentSimilarityIn {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "document_id")
    public Document document;
    public BigDecimal similarity;
    @Type(type = "org.eurecat.pathocert.backend.close_assessments.jpa.DocumentImpactJPA")
    public DocumentImpact impact;
    @Type(type = "org.eurecat.pathocert.backend.close_assessments.jpa.DocumentControlJPA")
    public DocumentControl control;

    public DocumentControl getControl() {
        return control;
    }

    public DocumentImpact getImpact() {
        return impact;
    }

    public Document getDocument() {
        return document;
    }
}
