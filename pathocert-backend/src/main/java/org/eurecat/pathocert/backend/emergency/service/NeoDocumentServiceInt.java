package org.eurecat.pathocert.backend.emergency.service;

import org.eurecat.pathocert.backend.close_assessments.data.DocumentControl;
import org.eurecat.pathocert.backend.close_assessments.data.DocumentImpact;
import org.eurecat.pathocert.backend.close_assessments.data.PathogenInfo;
import org.eurecat.pathocert.backend.emergency.model.LocationNode;

import java.util.List;

public interface NeoDocumentServiceInt {
    List<LocationNode> executeLocationTransaction(String query);

    DocumentImpact executeImpactTransaction(String query);

    PathogenInfo executePathogenImpactTransaction(String query);

    DocumentControl executeControlTransaction(String query);

    List<String> executeLabelTransaction(String query);
}
