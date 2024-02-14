package org.eurecat.pathocert.backend;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.eurecat.pathocert.backend.configuration.ConfigurationKt.parseProperties;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class BackendApplicationTests {

    @Autowired
    ConfigProperties properties;

    @DisplayName("Integration test. Tests if all the spring context can be loaded and initialized, included the DB")
    @Test
    void contextLoads() {
        assertEquals("neo4j", properties.getConfigValue("neo4j.expert.user"));
    }

}
