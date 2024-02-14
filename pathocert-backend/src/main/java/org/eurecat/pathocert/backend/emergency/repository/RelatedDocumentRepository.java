package org.eurecat.pathocert.backend.emergency.repository;

import org.eurecat.pathocert.backend.emergency.model.RelatedDocument;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import javax.persistence.Table;
import java.util.List;

@PreAuthorize("hasRole('SUPER_ADMIN')")
@Table
public interface RelatedDocumentRepository extends CrudRepository<RelatedDocument, Long> {

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    List<RelatedDocument> findByDocumentId(Long document);

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    List<RelatedDocument> findByEmergencyId(Long emergency);

}
