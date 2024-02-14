package org.eurecat.pathocert.backend.emergency.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;

import java.util.Objects;

// Represents a node in the expert graph
public class ExNode {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String description;

    public ExNode() {
    }

    public ExNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        return this.getClass().toString() + ": " + this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExNode exNode = (ExNode) o;
        return Objects.equals(name, exNode.name) && Objects.equals(description, exNode.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }

}
