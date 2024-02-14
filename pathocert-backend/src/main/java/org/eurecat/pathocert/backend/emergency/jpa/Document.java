package org.eurecat.pathocert.backend.emergency.jpa;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.eurecat.pathocert.backend.close_assessments.data.DocumentControl;
import org.eurecat.pathocert.backend.close_assessments.data.DocumentImpact;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "Document")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Document {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private Date data;
    private String source;
    private String url;
    private String keywords;
    @Column(columnDefinition = "TEXT")
    private String text;

    @Convert(converter = DocumentImpactConverter.class)
    @Column(columnDefinition = "TEXT")
    private DocumentImpact impact;
    @Convert(converter = DocumentControlConverter.class)
    @Column(columnDefinition = "TEXT")
    private DocumentControl control;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Document document = (Document) o;

        return Objects.equals(id, document.id);
    }

    @Override
    public int hashCode() {
        return 1422296640;
    }
}