package org.eurecat.pathocert.backend.close_assessments;

import org.eurecat.pathocert.backend.close_assessments.data.DocumentControl;
import org.eurecat.pathocert.backend.close_assessments.data.DocumentSimilarity;
import org.eurecat.pathocert.backend.emergency.jpa.AssessmentProgression;
import org.eurecat.pathocert.backend.emergency.jpa.Emergency;
import org.eurecat.pathocert.backend.emergency.repository.DocumentRepository;
import org.eurecat.pathocert.backend.emergency.repository.EmergencyRepository;
import org.eurecat.pathocert.backend.emergency.service.EmergencyComparatorInt;
import org.eurecat.pathocert.backend.emergency.service.NeoDocumentServiceInt;
import org.jobrunr.jobs.annotations.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class AssessmentService {

    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private EmergencyComparatorInt emergencyComparator;
    @Autowired
    private EmergencyRepository emergencyRepository;
    @Autowired
    private NeoDocumentServiceInt neoDocumentService;

    @Job(name = "Assessments of %1")
    public void calculateCloseAssessments(Long emergencyId, String emergencyIdString) {
        var emergency_o = emergencyRepository.findById(emergencyId);
        if (emergency_o.isEmpty()){
            return;
        }
        var emergency = emergency_o.get();
        Map<String, BigDecimal> similarities = new HashMap<>();
        var query = "MATCH (n:Document) RETURN n.title as label";
        var neo4jTitles = neoDocumentService.executeLabelTransaction(query);
        var futures = new LinkedList<CompletableFuture<DocSim>>();
        neo4jTitles.forEach(title -> {
            futures.add(new DocSimilarityThread(title, emergency, documentRepository, neoDocumentService, emergencyComparator).runSimil());
            getAndSaveDocumentImpact(title);
            getAndSaveDocumentControl(title);
        });
        for (CompletableFuture<DocSim> future : futures) {
            if (future == null) {
                continue;
            }
            future.join();
        }
        futures.forEach(future -> {
            try {
                if (future == null) {
                    return;
                }
                var fut = future.get();
                if (fut != null) {
                    similarities.put(fut.document_title, fut.similarity);
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        emergency.setSimilarityList(similarities);
        emergency.setAssessmentProgression(AssessmentProgression.ASSESSED);
        emergencyRepository.save(emergency);
    }

    private void getAndSaveDocumentImpact(String documentName){
        var query = "MATCH (n {title: \"" + documentName + "\"}) WITH n " +
                "MATCH (n)-[:HAS_IMPACT]->(n2) WITH n, n2 MATCH (n)-[:HAS_CONTAMINANT]->(n3) " +
                "RETURN n2.people_dead as label1, n2.people_hospitalized as label2, n2.people_ill as label3, n3.name as label4";
        var impact = neoDocumentService.executeImpactTransaction(query);
        documentRepository.findByNameEquals(documentName).ifPresent(doc -> {
            doc.setImpact(impact);
            documentRepository.save(doc);
        });
    }

    private void getAndSaveDocumentControl(String documentName) {
        var query = "MATCH (n {title: \"" + documentName + "\"})-[:HAS_MONITORING]->(n2) return n2.description as label";
        var monitoring = neoDocumentService.executeLabelTransaction(query);
        query = "MATCH (n {title: \"" + documentName + "\"})-[:HAS_RESTORATION]->(n2) return n2.description as label";
        var restoration = neoDocumentService.executeLabelTransaction(query);
        query = "MATCH (n {title: \"" + documentName + "\"})-[:HAS_PREVENTION]->(n2) return n2.description as label";
        var prevention = neoDocumentService.executeLabelTransaction(query);
        var control = new DocumentControl(monitoring, restoration, prevention);
        documentRepository.findByNameEquals(documentName).ifPresent(doc -> {
            doc.setControl(control);
            documentRepository.save(doc);
        });
    }
}
