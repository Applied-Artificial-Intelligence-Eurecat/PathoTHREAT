package org.eurecat.pathocert.backend.users.repository;

import org.eurecat.pathocert.backend.users.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.persistence.Table;
import java.util.Optional;

@Table
public interface UserRepository extends CrudRepository<User, Long> {


    Optional<User> findByUsername(String username);


    @NotNull
    //@PreAuthorize("hasRole('SUPER_ADMIN') or (hasRole('ADMIN') and principal.getOrgId() == #var1.getOrganization().getId())" + "or (principal.getUser().getId().equals(var1.getId()))")
    <S extends User> S save(@NotNull S var1);


    @NotNull
    @PreAuthorize("hasRole('SUPER_ADMIN') or (hasRole('ADMIN') and principal.getOrgId() == #var1.getOrganization().getId())")
    <S extends User> Iterable<S> saveAll(@NotNull Iterable<S> var1);

    @NotNull
    @PostAuthorize("hasRole('SUPER_ADMIN') or !returnObject.isPresent()" + "or (hasRole('ADMIN') and principal.getOrgId() == returnObject.get().getOrganization().getId())")
    Optional<User> findById(@NotNull Long var1);

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    boolean existsById(@NotNull Long var1);

    @NotNull
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    Iterable<User> findAll();

    @NotNull
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    Iterable<User> findAllById(@NotNull Iterable<Long> var1);

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    long count();

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    void deleteById(@NotNull Long var1);

    @PreAuthorize("hasRole('SUPER_ADMIN') or (hasRole('ADMIN') and principal.getOrgId() == #var1.getOrganization().getId())")
    void delete(@NotNull User var1);

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    void deleteAll(@NotNull Iterable<? extends User> var1);

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    void deleteAll();
}
