package org.eurecat.pathocert.backend.repository;

import org.eurecat.pathocert.backend.users.model.Organization;
import org.eurecat.pathocert.backend.users.model.User;
import org.eurecat.pathocert.backend.users.repository.OrganizationRepository;
import org.eurecat.pathocert.backend.users.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@DataJpaTest
public class UserOrganizationRelationship {

    @Autowired
    UserRepository repository;

    @Autowired
    OrganizationRepository orgRepository;

    @DisplayName("Test if organization is retrieved correcly from a user")
    @Test
    void someTest() {
        var user = new User();
        var org = new Organization();
        orgRepository.save(org);
        user.setOrganization(org);
        repository.save(user);
        var userGot = repository.findById(user.getId());
        assertEquals(Optional.of(org), userGot.map(User::getOrganization));
    }

}
