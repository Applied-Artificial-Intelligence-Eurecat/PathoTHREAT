package org.eurecat.pathocert.backend.emergency.service;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

public class NeoDriver implements AutoCloseable {

    private final Driver driver;

    public NeoDriver(Driver driver) {
        this.driver = driver;
    }

    @Override
    public void close() {
        driver.close();
    }

    public Session session() {
        return driver.session();
    }
}
