package org.eurecat.pathocert.backend.emergency.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.eurecat.pathocert.backend.emergency.jpa.Document;
import org.eurecat.pathocert.backend.emergency.jpa.Emergency;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;


@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class RelatedDocument {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "documentId")
    @NotNull
    private Document document;

    @ManyToOne(optional = false)
    @JoinColumn(name = "emergencyId")
    @NotNull
    private Emergency emergency;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RelatedDocument that = (RelatedDocument) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 2095578161;
    }
}
