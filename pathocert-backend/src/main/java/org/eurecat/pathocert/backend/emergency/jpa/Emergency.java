package org.eurecat.pathocert.backend.emergency.jpa;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.eurecat.pathocert.backend.close_assessments.data.DocumentSimilarity;
import org.eurecat.pathocert.backend.users.model.Organization;
import org.eurecat.pathocert.backend.users.model.User;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "Emergency")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Emergency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long reportDate;

    @Column(columnDefinition = "TEXT")
    private String emergencyName;
    private String scenario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_id")
    private Organization reportingOrganization;

    private String reportingUsername;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User reportingUserId;
    private String nameReporter;
    @Column(columnDefinition = "TEXT")
    private String emergencyTypeClass;

    @Column(columnDefinition = "TEXT")
    private String emergencyDescription;

    @Column(columnDefinition = "TEXT")
    private String waterStateDescription;
    private BigDecimal affectedAreaLat;
    private BigDecimal affectedAreaLon;
    @Column(columnDefinition = "TEXT")
    private String ocupationDescription;
    @Column(columnDefinition = "TEXT")
    private String riskAssessment;
    @Column(columnDefinition = "TEXT")
    private String actionPlan;

    private boolean archived;

    @Convert(converter = StringStringConverter.class)
    @Column(columnDefinition = "text")
    private Set<String> infrastructureConcerns;
    @Convert(converter = StringStringConverter.class)
    @Column(columnDefinition = "text")
    private Set<String> symptoms;
    @Convert(converter = StringStringConverter.class)
    @Column(columnDefinition = "text")
    private Set<String> contaminants;
    @Convert(converter = StringStringConverter.class)
    @Column(columnDefinition = "text")
    private Set<String> infrastructures;

    @Convert(converter = MapStringConverter.class)
    @Column(columnDefinition = "text")
    private Map<String, BigDecimal> similarityList;
    private AssessmentProgression assessmentProgression = AssessmentProgression.NOT_ASSESSED;


    // Save impact and control as plain richtext, as it can and it will be edited manually.
    @Column(columnDefinition = "TEXT")
    private String impact;
    @Column(columnDefinition = "TEXT")
    private String control;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Emergency emergency = (Emergency) o;

        return Objects.equals(id, emergency.id);
    }

    @Override
    public int hashCode() {
        return 192775690;
    }
}
