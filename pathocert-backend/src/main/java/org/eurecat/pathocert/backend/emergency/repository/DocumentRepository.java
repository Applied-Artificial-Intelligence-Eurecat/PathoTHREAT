package org.eurecat.pathocert.backend.emergency.repository;

import org.eurecat.pathocert.backend.emergency.jpa.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.persistence.Table;
import java.util.Optional;

//@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
@Table
@Profile("!(test | assessment-test)")
public interface DocumentRepository extends CrudRepository<Document, Long> {
    Optional<Document> findByNameEquals(String name);

    Optional<Document> findBySourceEquals(String source);
}
