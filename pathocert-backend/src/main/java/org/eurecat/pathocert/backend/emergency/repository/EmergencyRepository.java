package org.eurecat.pathocert.backend.emergency.repository;

import org.eurecat.pathocert.backend.emergency.jpa.Emergency;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.persistence.Table;
import java.util.List;
import java.util.Optional;

@Table
@Profile("!assessment-test")
public interface EmergencyRepository extends CrudRepository<Emergency, Long> {


    List<Emergency> findByReportingOrganization_IdAndArchived(Long id, boolean archived);

    List<Emergency> findByReportingUsernameAndArchived(String username, boolean archived);


    List<Emergency> findByArchived(Boolean archived);

    @NotNull
    /*@PreAuthorize("hasRole('SUPER_ADMIN') " +
            "or hasRole('ADMIN') and " +
            "principal.getOrgId() == #var1.getReportingOrganization().getId()")*/
    <S extends Emergency> S save(@NotNull S var1);

    @NotNull
    @PreAuthorize("hasRole('SUPER_ADMIN') ")
    <S extends Emergency> Iterable<S> saveAll(@NotNull Iterable<S> var1);

    @NotNull
    //@PostAuthorize("hasRole('SUPER_ADMIN') or !returnObject.isPresent()" + "or (hasRole('ADMIN') and principal.getOrgId() == returnObject.get().getReportingOrganization().getId())")
    Optional<Emergency> findById(@NotNull Long var1);

    @PostAuthorize("hasRole('SUPER_ADMIN') or !returnObject.isPresent()" + "or (hasRole('ADMIN') and principal.getOrgId() == returnObject.get().getReportingOrganization().getId())")
    boolean existsById(@NotNull Long var1);

    @NotNull
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    Iterable<Emergency> findAll();

    @NotNull
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    Iterable<Emergency> findAllById(@NotNull Iterable<Long> var1);

    //@PreAuthorize("hasRole('SUPER_ADMIN')")
    long count();

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    void deleteById(@NotNull Long var1);

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    void delete(@NotNull Emergency var1);

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    void deleteAll(@NotNull Iterable<? extends Emergency> var1);

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    void deleteAll();
}
