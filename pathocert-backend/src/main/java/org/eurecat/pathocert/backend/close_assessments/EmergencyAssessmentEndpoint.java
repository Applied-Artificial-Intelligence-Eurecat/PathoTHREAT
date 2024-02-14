package org.eurecat.pathocert.backend.close_assessments;

import org.eurecat.pathocert.backend.close_assessments.data.DocumentControl;
import org.eurecat.pathocert.backend.close_assessments.data.DocumentImpact;
import org.eurecat.pathocert.backend.close_assessments.data.DocumentSimilarity;
import org.eurecat.pathocert.backend.emergency.jpa.AssessmentProgression;
import org.eurecat.pathocert.backend.emergency.jpa.Emergency;
import org.eurecat.pathocert.backend.emergency.model.DocumentCombination;
import org.eurecat.pathocert.backend.emergency.model.ImpactCombination;
import org.eurecat.pathocert.backend.emergency.model.LocationNode;
import org.eurecat.pathocert.backend.emergency.repository.DocumentRepository;
import org.eurecat.pathocert.backend.emergency.repository.EmergencyRepository;
import org.eurecat.pathocert.backend.emergency.service.EmergencyComparatorInt;
import org.eurecat.pathocert.backend.emergency.service.NeoDocumentServiceInt;
import org.jobrunr.scheduling.JobScheduler;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.*;

/**
 * This class contains all the endpoints necessary to make an assessment.
 * Per se, it is where all the magic happens.
 * <p>
 * The use case of an assessment is typically this:
 * Preconditions: There is an emergency that needs a plan to reverse/mitigate it.
 * 1. The user asks which emergencies are more similar to it.
 * 2. The system answers a collection of emergencies (G) (? or documents) that are close to it.
 * 3. The user selects a subgroup of emergencies (G' <= G) and sends them to the system.
 * 4. The system analysis them and performs a document of how to perform the solution
 * 5. The user revises them and archives the emergency | puts them ongoing
 *
 * @author sergi.simon
 */
@RequestMapping(value = "/api/assessment/")
@BasePathAwareController
public class EmergencyAssessmentEndpoint {

    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private EmergencyComparatorInt emergencyComparator;
    @Autowired
    private EmergencyRepository emergencyRepository;
    @Autowired
    private NeoDocumentServiceInt neoDocumentService;
    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private JobScheduler jobScheduler;

    @GetMapping(path = "{emergencyId}/close-assessments")
    @ResponseBody
    @Cacheable("close-assessments")
    private ResponseEntity<CollectionModel<DocumentSimilarity>> getCloseAssessments(@PathVariable("emergencyId") Long emergencyId) {
        System.out.println("RECEIVED REQUEST");
        Optional<Emergency> emergency_o = emergencyRepository.findById(emergencyId);
        if (emergency_o.isPresent()) {
            Emergency emergency = emergency_o.get();
            if (emergency.getAssessmentProgression() == AssessmentProgression.ASSESSED) {
                System.out.println("EMERGENCY IS ASSESSED");
                var calculatedSimilarities = emergency.getSimilarityList();
                System.out.println("SIMILS: " + calculatedSimilarities.toString());
                List<DocumentSimilarity> similarities = new LinkedList<>();
                int i = 0;
                for (String documentTitle : calculatedSimilarities.keySet()) {
                    System.out.println("TITLE: " + documentTitle);
                    documentRepository.findByNameEquals(documentTitle).ifPresentOrElse(e -> {
                        System.out.println("DOCUMENT IS PRESENT");
                        similarities.add(new DocumentSimilarity(
                                e,
                                calculatedSimilarities.get(documentTitle),
                                e.getImpact(),
                                e.getControl()));
                        }, () -> {
                        System.out.println("DOCUMENT NOT PRESENT");
                    });
                    i++;
                }
                similarities.sort(Comparator.comparing(DocumentSimilarity::getSimilarity).reversed());
                return ResponseEntity.ok(CollectionModel.of(similarities));
            } else if (emergency.getAssessmentProgression() == AssessmentProgression.NOT_ASSESSED) {
                System.out.println("EMERGENCY IS NOT ASSESSED");
                emergency.setAssessmentProgression(AssessmentProgression.IN_PROGRESS);
                emergencyRepository.save(emergency);
                jobScheduler.enqueue(() -> assessmentService.calculateCloseAssessments(emergency.getId(), emergency.getId().toString()));
                System.out.println("JOB ENQUEUED");
                return ResponseEntity.ok(CollectionModel.of(new LinkedList<>()));
            } else {
                System.out.println("EMERGENCY IS IN PROGRESS");
            }
            System.out.println(emergency.getSimilarityList());
            return ResponseEntity.ok(CollectionModel.of(new LinkedList<>()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private DocumentImpact getDocumentImpact(String documentName) {
        var query = "MATCH (n {title: \"" + documentName + "\"}) WITH n " +
                "MATCH (n)-[:HAS_IMPACT]->(n2) WITH n, n2 MATCH (n)-[:HAS_CONTAMINANT]->(n3) " +
                "RETURN n2.people_dead as label1, n2.people_hospitalized as label2, n2.people_ill as label3, n3.name as label4";
        return neoDocumentService.executeImpactTransaction(query);
    }


    private DocumentControl getDocumentControl(String documentName) {
        var query = "MATCH (n {title: \"" + documentName + "\"})-[:HAS_MONITORING]->(n2) return n2.description as label";
        var monitoring = neoDocumentService.executeLabelTransaction(query);
        query = "MATCH (n {title: \"" + documentName + "\"})-[:HAS_RESTORATION]->(n2) return n2.description as label";
        var restoration = neoDocumentService.executeLabelTransaction(query);
        query = "MATCH (n {title: \"" + documentName + "\"})-[:HAS_PREVENTION]->(n2) return n2.description as label";
        var prevention = neoDocumentService.executeLabelTransaction(query);
        return new DocumentControl(monitoring, restoration, prevention);
    }

    /**
     * Precondition: documents is not empty
     *
     * @param documentNames : List of the names of the documents that the new document should be constructed of.
     *                      as the client has already the names, and it is only used that, it is pointless to give
     *                      more information.
     * @return A DocumentCombination entity
     */
    @PostMapping(path = "merge-documents")
    @ResponseBody
    private ResponseEntity<EntityModel<DocumentCombination>> constructDocument(@RequestBody ArrayList<String> documentNames) throws IOException, InterruptedException, ParseException {
        if (documentNames.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        JSONObject object = makeRequestToNeo4j(documentNames, client);
        final ImpactCombination impact1 = getImpactCombination(object);
        final LocationNode locationNode = getLocationNode(object);
        DocumentCombination documentCombination = getDocumentCombination(object, impact1, locationNode);
        return ResponseEntity.ok(EntityModel.of(documentCombination));
    }

    private DocumentCombination getDocumentCombination(JSONObject object, ImpactCombination impact1, LocationNode locationNode) {
        return DocumentCombination
                .builder()
                .cause(Arrays.asList(((String) object.get("Cause")).split(",")))
                .contaminants(Arrays.asList(((String) object.get("ContaminantD")).split(",")))
                .detection(Arrays.asList(((String) object.get("Detection")).split(",")))
                .impact(impact1)
                .location(locationNode)
                .mitigation(Arrays.asList(((String) object.get("Mitigation")).split(",")))
                .monitoring(Arrays.asList(((String) object.get("Monitoring")).split(",")))
                .prevention(Arrays.asList(((String) object.get("Prevention")).split(",")))
                .restoration(Arrays.asList(((String) object.get("Restoration")).split(",")))
                .source(Arrays.asList(((String) object.get("Source")).split(",")))
                .symptoms(Arrays.asList(((String) object.get("SymptomD")).split(",")))
                .build();
    }

    private JSONObject makeRequestToNeo4j(List<String> documentNames, HttpClient client) throws IOException, InterruptedException, ParseException {
        StringBuilder body = new StringBuilder();
        for (var documentName : documentNames) {
            body.append(documentName); // This probably has security reasons
            body.append("|");
        }
        String bodyString = body.substring(0, body.lastIndexOf("|"));
        return emergencyComparator.mergeInNeo4j(bodyString, client);
    }

    private LocationNode getLocationNode(JSONObject object) {
        JSONObject location = (JSONObject) object.get("Location");
        if (location == null) {
            return new LocationNode("", "", "");
        }
        return new LocationNode(
                ((String) location.get("city"))
                , ((String) location.get("region"))
                , ((String) location.get("country"))
        );
    }

    private ImpactCombination getImpactCombination(JSONObject object) {
        JSONObject impact = (JSONObject) object.get("Impact");
        if (impact == null) {
            return ImpactCombination.builder().build();
        }
        return ImpactCombination.builder()
                .peopleIll((String) impact.get("people_ill"))
                .peopleHospitalized((String) impact.get("people_hospitalized"))
                .peopleDead((String) impact.get("people_dead"))
                .build();
    }
}
