package org.eurecat.pathocert.backend.users.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "organizations")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private ActionArea actionArea;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public ActionArea getActionArea() {
        return actionArea;
    }

    public void setActionArea(ActionArea actionArea) {
        this.actionArea = actionArea;
    }


}
