package org.eurecat.pathocert.backend.users.repository;

import org.eurecat.pathocert.backend.users.model.Organization;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.persistence.Table;
import java.util.Optional;

//@PreAuthorize("hasRole('SUPER_ADMIN')")
@Table
public interface OrganizationRepository extends CrudRepository<Organization, Long> {


    @NotNull
    //@PreAuthorize("hasRole('SUPER_ADMIN')")
    <S extends Organization> S save(@NotNull S var1);

    @NotNull
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    <S extends Organization> Iterable<S> saveAll(@NotNull Iterable<S> var1);

    @NotNull
    @Override
    //@PreAuthorize("hasRole('SUPER_ADMIN') or principal.getOrgId() == #var1")
    Optional<Organization> findById(@NotNull Long var1);

    @PreAuthorize("hasRole('SUPER_ADMIN') or principal.getOrgId() == #var1")
    boolean existsById(@NotNull Long var1);

    @NotNull
    //@PreAuthorize("hasRole('SUPER_ADMIN')")
    Iterable<Organization> findAll();

    @NotNull
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    Iterable<Organization> findAllById(@NotNull Iterable<Long> var1);

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    long count();

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    void deleteById(@NotNull Long var1);

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    void delete(@NotNull Organization var1);

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    void deleteAll(@NotNull Iterable<? extends Organization> var1);

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    void deleteAll();

}
