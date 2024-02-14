package org.eurecat.pathocert.backend.close_assessments.data;

import java.util.List;

public class PathogenInfo {
    public List<String> pathogens;
    public List<String> symptoms;

    public PathogenInfo(List<String> pathogens, List<String> symptoms){
        this.pathogens = pathogens;
        this.symptoms = symptoms;
    }
}
