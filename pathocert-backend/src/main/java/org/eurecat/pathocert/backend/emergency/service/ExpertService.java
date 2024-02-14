package org.eurecat.pathocert.backend.emergency.service;

import org.eurecat.pathocert.backend.ConfigProperties;
import org.eurecat.pathocert.backend.emergency.model.ExNode;
import org.eurecat.pathocert.backend.emergency.model.GraphItem;
import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Profile("!test")
public class ExpertService implements ExpertServiceInt {
    @Autowired
    ConfigProperties properties;
    private String neo4juri = "bolt://pathocert-neo:7687";
    private String neo4juser = "neo4j";
    private String neo4jpass = "1234";

    public ExpertService() {
        try {
            this.neo4juri = properties.getConfigValue("neo4j.expert.uri");
            this.neo4juser = properties.getConfigValue("neo4j.expert.user");
            this.neo4jpass = properties.getConfigValue("neo4j.expert.pass");
        } catch (NullPointerException ignored) {
        }
    }

    public List<ExNode> findAll() {
        return executeNodeTransaction("MATCH (n) return n");
    }

    public List<String> getTermLabel(String term1) {
        return executeLabelTransaction("MATCH (n {name: \"" + term1 + "\"}) return LABELS(n) as label");
    }

    public int valueOfLabel(String label) {
        switch (label) {
            case "Event":
                return 0;
            case "CascadingWNEvent":
                return 1;
            case "ProducedEvent":
                return 2;
            case "Contaminant":
                return 3;
            case "EffectHealth":
            case "ContaminantTreatment":
            case "ContaminantFamily":
            case "EffectWaterOdor":
            case "ContaminantMitigation":
                return 4;
            case "Symptom":
                return 5;
            case "ContaminantType":
            case "EffectWaterTaste":
                return 6;
        }
        return -1;
    }

    public String getQuery(String term1, String term2) {
        return "MATCH (n {name: \"" + term1 + "\"})" + getQueryInterior(term1, term2) + "(n2:" + term2 + ") return n2";
    }

    @Override
    public List<GraphItem> findPath(String term1, String term2) {
        String query = "MATCH path = (n {name: \"" + term1 + "\"})" + getQueryInterior(term1, term2) + "(n2:" + term2 + ") return path";
        return executePathTransaction(query);
    }

    public String getQueryInterior(String term1, String term2) {
        final var termList = getTermLabel(term1);
        if (termList.isEmpty()) {
            return null;
        }
        for (String term1Label : termList) {
            switch (valueOfLabel(term1Label)) {
                case 0:
                    return "-[*]->";
                case 1:
                case 2:
                case 3:
                    if (valueOfLabel(term2) < valueOfLabel(term1Label)) {
                        return "<-[*]-";
                    } else {
                        return "-[*]->";
                    }
                case 4:
                    if (valueOfLabel(term2) < 4) {
                        return "<-[*]-";
                    } else if (valueOfLabel(term2) == 4) {
                        if (term2.equals(term1Label)) {
                            return "<-[*0]-";
                        } else {
                            return "<-[*]-(nc:Contaminant)-[*]->";
                        }
                    } else if (valueOfLabel(term2) == 5) {
                        if (term1Label.equals("EffectHealth")) {
                            return "-[*]->";
                        } else {
                            return "<-[*]-(nc:Contaminant)-[*]->";
                        }
                    } else {
                        if (term1Label.equals("ContaminantFamily")) {
                            return "-[*]->";
                        } else {
                            return "<-[*]-(nc:Contaminant)-[*]->";
                        }
                    }
                case 5:
                    if (valueOfLabel(term1Label) < 4 || term1Label.equals("EffectHealth")) {
                        return "<-[*]-";
                    } else {
                        return "<-[*]-(nc:Contaminant)-[*]->";
                    }
                case 6:
                    if (valueOfLabel(term1Label) < 4 || term1Label.equals("ContaminantFamily")) {
                        return "<-[*]-";
                    } else if (valueOfLabel(term2) == valueOfLabel(term1Label)) {
                        return "<-[*]-(nc:ContaminantFamily)-[*]->";
                    } else {
                        return "<-[*]-(nc:Contaminant)-[*]->";
                    }
            }
        }
        return null;
    }

    public List<ExNode> getTerm1andTerm2Search(String term1, String term2) {
        return executeNodeTransaction(getQuery(term1, term2));
    }

    @Override
    public List<GraphItem> getRelationsOfType(String rel_type) {
        return executeRelationshipTransaction("MATCH (n)-[:" + rel_type + "]->(n2) RETURN n.name AS source, n2.name AS target");
    }

    public List<ExNode> executeNodeTransaction(String query) {
        Driver driver = GraphDatabase.driver(this.neo4juri, AuthTokens.basic(this.neo4juser, this.neo4jpass));
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> {
                List<ExNode> nodes1 = new LinkedList<>();
                Result result = tx.run(query);
                for (Record record : result.list()) {
                    Map<String, Object> nodeMap = record.values().get(0).asMap();
                    ExNode node = new ExNode();
                    node.setName(((String) nodeMap.get("name")));
                    boolean found = false;
                    for (ExNode nd : nodes1) {
                        if (nd.equals(node)) {
                            found = true;
                            break;
                        }
                    }
                    if (found) continue;
                    nodes1.add(node);
                }
                return nodes1;
            });
        }
    }

    public List<GraphItem> executePathTransaction(String query) {
        Driver driver = GraphDatabase.driver(this.neo4juri, AuthTokens.basic(this.neo4juser, this.neo4jpass));
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> {
                List<GraphItem> graphItems = new LinkedList<>();
                Result result = tx.run(query);
                for (Record record : result.list()) {
                    Path path = record.get("path").asPath();
                    Iterable<Node> nodes = path.nodes();
                    for (Node node : nodes) {
                        // Process each node in the path
                        // You can access node properties using node.get("property_name")
                        boolean repeated = false;
                        GraphItem newItem = new GraphItem("nodes", node.get("name").asString(), node.labels().iterator().next());
                        for (GraphItem item : graphItems) {
                            if (item.equals(newItem)) {
                                repeated = true;
                                break;
                            }
                        }
                        if (!repeated) {
                            graphItems.add(newItem);
                        }
                    }

                    Iterable<Relationship> relationships = path.relationships();
                    for (Relationship relationship : relationships) {
                        // Process each relationship in the path
                        // You can access relationship properties using relationship.get("property_name")
                        Result sourceResult = tx.run("MATCH (s) WHERE id(s) = " + relationship.startNodeId() + " RETURN s.name AS name");
                        Record sourceRecord = sourceResult.next();
                        String sourceNodeName = sourceRecord.get("name").asString();
                        Result targetResult = tx.run("MATCH (s) WHERE id(s) = " + relationship.endNodeId() + " RETURN s.name AS name");
                        Record targetRecord = targetResult.next();
                        String targetNodeName = targetRecord.get("name").asString();
                        GraphItem rel = new GraphItem("edges", sourceNodeName, targetNodeName);
                        rel.label = relationship.type();
                        boolean repeated = false;
                        for (GraphItem item : graphItems) {
                            if (item.equals(rel)) {
                                repeated = true;
                                break;
                            }
                        }
                        if (!repeated) {
                            graphItems.add(rel);
                        }
                    }
                }
                for (GraphItem item : graphItems) {
                    System.out.println(item.toString());
                }
                return graphItems;
            });
        }
    }

    public List<GraphItem> executeRelationshipTransaction(String query) {
        Driver driver = GraphDatabase.driver(this.neo4juri, AuthTokens.basic(this.neo4juser, this.neo4jpass));
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> {
                List<GraphItem> nodes = new LinkedList<>();
                Result result = tx.run(query);
                System.out.println(result);
                for (Record record : result.list()) {
                    String source = record.get("source").asString();
                    String target = record.get("target").asString();
                    System.out.println("Source " + source + "\tTarget " + target);
                    if (!Objects.equals(source, "null") && !Objects.equals(target, "null")) {
                        nodes.add(new GraphItem("edges", source, target));
                    }
                }
                return nodes;
            });
        }
    }

    public List<String> executeLabelTransaction(String query) {
        Driver driver = GraphDatabase.driver(this.neo4juri, AuthTokens.basic(this.neo4juser, this.neo4jpass));
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> {
                List<String> nodes = new LinkedList<>();
                Result result = tx.run(query);
                for (Record record : result.list()) {
                    String label = record.get("label").values().iterator().next().asString();
                    nodes.add(label);
                }
                return nodes;
            });
        }
    }
}
