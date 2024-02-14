package org.eurecat.pathocert.backend.emergency.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ImpactCombination {
    private String peopleIll;
    private String peopleHospitalized;
    private String peopleDead;
}
