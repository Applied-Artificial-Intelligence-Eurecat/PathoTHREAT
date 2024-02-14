package org.eurecat.pathocert.backend.emergency.service;

import org.eurecat.pathocert.backend.emergency.model.ExNode;
import org.eurecat.pathocert.backend.emergency.model.GraphItem;

import java.util.List;

public interface ExpertServiceInt {
    List<ExNode> getTerm1andTerm2Search(String term1, String term2);

    List<GraphItem> getRelationsOfType(String rel_type);

    public String getQuery(String term1, String term2);

    List<GraphItem> findPath(String term1, String term2);
}
