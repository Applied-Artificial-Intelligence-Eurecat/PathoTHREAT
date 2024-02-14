package org.eurecat.pathocert.backend.repository;

import org.eurecat.pathocert.backend.users.model.User;
import org.eurecat.pathocert.backend.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@DataJpaTest
class UserRepositoryTest {


    int userNumber;

    Supplier<User> create = () -> {
        var user = new User();
        user.setUsername("haha" + userNumber);
        userNumber++;
        user.setPassword("Just a nobody");
        return user;
    };

    @Autowired
    UserRepository repository;

    @BeforeEach
    void setUp() {
        userNumber = 0;
        repository.deleteAll();
    }

    @Test
    void injectedComponentsAreNotNull() {
        assertNotNull(repository);
    }

    @DisplayName("Save entity then check if exists")
    @Test
    void saveAndExistTest() {
        var user = create.get();
        repository.save(user);
        assertTrue(repository.existsById(user.getId()));
    }

    @DisplayName("Saved and retrieved are equals")
    @Test
    void saveAndRetrieveTest() {
        var user = create.get();
        repository.saveAll(List.of(user, create.get(), create.get()));
        assertEquals(3, repository.count());
        assertTrue(repository.existsById(user.getId()));
        assertEquals(Optional.of(user), repository.findById(user.getId()));
    }

    @DisplayName("Save, retrieve all, delete by entity, id, and all")
    @Test
    void deleteIterableTest() {
        repository.saveAll(List.of(create.get(), create.get()));
        assertEquals(2, repository.count());
        var users = repository.findAll();
        repository.deleteAll(users);
        assertEquals(0, repository.count());

    }

    @DisplayName("Delete all")
    @Test
    void deleteAll() {
        repository.saveAll(List.of(create.get(), create.get()));
        assertEquals(2, repository.count());
        repository.deleteAll();
        assertEquals(0, repository.count());
    }

    @DisplayName("Delete by user")
    @Test
    void deleteByUser() {
        repository.saveAll(List.of(create.get(), create.get()));
        assertEquals(2, repository.count());
        repository.findAll().forEach(u -> repository.delete(u));
        assertEquals(0, repository.count());
    }

    @DisplayName("Delete by id")
    @Test
    void deleteById() {
        repository.saveAll(List.of(create.get(), create.get()));
        assertEquals(2, repository.count());
        repository.findAll().forEach(u -> repository.deleteById(u.getId()));
        assertEquals(0, repository.count());
    }

    @DisplayName("Search by username")
    @Test
    void findByUsername() {
        repository.saveAll(List.of(create.get(), create.get()));
        var user1 = repository.findByUsername("haha");
        assertEquals(Optional.empty(), user1);
        var user2 = repository.findByUsername("haha0");
        assertNotEquals(Optional.empty(), user2);

    }


}