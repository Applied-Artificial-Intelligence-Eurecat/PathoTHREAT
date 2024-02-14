package org.eurecat.pathocert.backend.emergency.service;

import org.eurecat.pathocert.backend.emergency.jpa.Document;
import org.eurecat.pathocert.backend.emergency.jpa.Emergency;
import org.eurecat.pathocert.backend.emergency.model.LocationNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

@Service
@Profile("!(test | assessment-test)")
public class EmergencyComparator implements EmergencyComparatorInt {

    @Autowired
    private NeoDocumentServiceInt neoDocumentService;

    @NotNull
    public static String getBodyEmergencyDocument(Emergency emergency, Document document) {
        StringBuilder bodyR = new StringBuilder();
        bodyR.append("{\"document_title\": \"").append(document.getName()).append("\",");
        //bodyR.append("\"location_level\": \"").append(emergency.getAffectedAreaType()).append("\",");
        //bodyR.append("\"location\": \"").append(emergency.getNameAreaAffected()).append("\",");
        var detection = new LinkedList<>(emergency.getInfrastructures());
        bodyR.append("\"detection\": [");
        var isFirst = true;
        for (String cont : detection) {
            if (isFirst) {
                bodyR.append("\"").append(cont).append("\"");
                isFirst = false;
            } else {
                bodyR.append(", \"").append(cont).append("\"");
            }
        }
        bodyR.append("], ");
        var contaminants = new LinkedList<>(emergency.getContaminants());
        bodyR.append("\"contaminants\": [");
        isFirst = true;
        for (String cont : contaminants) {
            if (isFirst) {
                bodyR.append("\"").append(cont).append("\"");
                isFirst = false;
            } else {
                bodyR.append(", \"").append(cont).append("\"");
            }
        }
        bodyR.append("],");
        bodyR.append("\"type_of_event\": \"").append(emergency.getEmergencyTypeClass()).append("\",");
        var infrastructureConcerns = new LinkedList<>(emergency.getInfrastructureConcerns());
        bodyR.append("\"infrastructure\": [");
        isFirst = true;
        for (String cont : infrastructureConcerns) {
            if (isFirst) {
                bodyR.append("\"").append(cont).append("\"");
                isFirst = false;
            } else {
                bodyR.append(", \"").append(cont).append("\"");
            }
        }
        bodyR.append("]}");
        return bodyR.toString();
    }

    @Nullable
    private String getDocumentAffectedName(LocationNode documentLocation, String documentAffectedType) {
        String documentAffectedName;
        switch (documentAffectedType) {
            case "CITY":
                documentAffectedName = documentLocation.getCity();
                break;
            case "REGION":
                documentAffectedName = documentLocation.getRegion();
                break;
            default:
                documentAffectedName = documentLocation.getCountry();
        }
        return documentAffectedName;
    }

    public BigDecimal compareContaminants(Emergency emergency, Document document) {
        String query = "MATCH (n {title: \"" + document.getName() + "\"})-[:HAS_CONTAMINANT]->(n2) return n2.name as label";
        List<String> docContaminants = neoDocumentService.executeLabelTransaction(query);
        BigDecimal result = new BigDecimal(0);
        for (String contaminant : docContaminants) {
            if (emergency.getContaminants().contains(contaminant)) {
                result = result.add(new BigDecimal(100));
            }
        }
        if (docContaminants.size() == 0) {
            return result;
        }
        return result.divide(BigDecimal.valueOf(docContaminants.size()), RoundingMode.HALF_UP);
    }

    /**
     * Compare an emergency and a document using the service
     * It creates a JSON like:
     * {
     * "document_title": title of the document,
     * "location_level": STREET, CITY, or REGION,
     * "location": name of the location specified by the FR,
     * "contaminants": [List of contaminants that the FR ticked]
     * }
     *
     * @param emergency The emergency entered to the system by the FR
     * @param document  The document in the database
     * @return A percentage number
     */
    public BigDecimal compareService(Emergency emergency, Document document) {
        String jsonString = getBodyEmergencyDocument(emergency, document);
        return getResultFromService(jsonString);
    }

    @Override
    public JSONObject mergeInNeo4j(String bodyString, HttpClient client) throws ParseException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://pathocert-merge:5000/merge"))
                .method("POST", HttpRequest.BodyPublishers.ofString(bodyString))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        var ob = (JSONObject) new JSONParser()
                .parse(response.body());
        return ob;
    }

    @NotNull
    private BigDecimal getResultFromService(String jsonString) {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://pathocert-merge:5000/compare"))
                .method("POST", HttpRequest.BodyPublishers.ofString(jsonString))
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        assert response != null;
        return BigDecimal.valueOf(Double.parseDouble(response.body()));
    }
}
