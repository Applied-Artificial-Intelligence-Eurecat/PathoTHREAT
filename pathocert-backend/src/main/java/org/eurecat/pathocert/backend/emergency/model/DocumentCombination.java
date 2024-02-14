package org.eurecat.pathocert.backend.emergency.model;

import lombok.Builder;
import lombok.Data;
import org.eurecat.pathocert.backend.emergency.model.ImpactCombination;
import org.eurecat.pathocert.backend.emergency.model.LocationNode;

import java.util.List;

//Class to represent combined Neo4j data of documents
@Builder
@Data
public class DocumentCombination {
    private String documentNames;
    private LocationNode location;
    private List<String> cause;
    private List<String> source;
    private List<String> detection;
    private ImpactCombination impact;
    private List<String> contaminants;
    private List<String> symptoms;
    private List<String> mitigation;
    private List<String> monitoring;
    private List<String> restoration;
    private List<String> prevention;
}
